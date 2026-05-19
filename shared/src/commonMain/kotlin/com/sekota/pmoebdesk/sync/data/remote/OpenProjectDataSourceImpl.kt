package com.sekota.pmoebdesk.sync.data.remote

import com.sekota.pmoebdesk.sync.data.remote.model.*
import com.sekota.pmoebdesk.sync.domain.model.WorkPackage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class OpenProjectDataSourceImpl(
    private val client: HttpClient,
    private val host: String,
    private val apiKey: String
) : OpenProjectDataSource {

    @OptIn(ExperimentalEncodingApi::class)
    private val authHeader = "Basic " + Base64.encode("apikey:$apiKey".encodeToByteArray())

    override suspend fun fetchStatuses(): Map<String, String> {
        val response: HttpResponse = client.get("$host/api/v3/statuses") {
            header(HttpHeaders.Authorization, authHeader)
        }
        return if (response.status == HttpStatusCode.OK) {
            val body = response.body<JsonObject>()
            val embedded = body["_embedded"]?.jsonObject
            val elements = embedded?.get("elements")?.jsonArray
            elements?.filterIsInstance<JsonObject>()?.associate { obj: JsonObject ->
                val name = obj["name"]!!.jsonPrimitive.content.lowercase()
                val href = obj["_links"]!!.jsonObject["self"]!!.jsonObject["href"]!!.jsonPrimitive.content
                name to href
            } ?: emptyMap()
        } else {
            emptyMap()
        }
    }

    override suspend fun fetchTypes(): Map<String, Int> {
        val response: HttpResponse = client.get("$host/api/v3/types") {
            header(HttpHeaders.Authorization, authHeader)
        }
        return if (response.status == HttpStatusCode.OK) {
            val body = response.body<JsonObject>()
            val embedded = body["_embedded"]?.jsonObject
            val elements = embedded?.get("elements")?.jsonArray
            elements?.filterIsInstance<JsonObject>()?.associate { obj: JsonObject ->
                val name = obj["name"]!!.jsonPrimitive.content.lowercase()
                val id = obj["id"]!!.jsonPrimitive.int
                name to id
            } ?: emptyMap()
        } else {
            emptyMap()
        }
    }

    override suspend fun getProject(identifier: String): Int? {
        val response: HttpResponse = client.get("$host/api/v3/projects/$identifier") {
            header(HttpHeaders.Authorization, authHeader)
        }
        return if (response.status == HttpStatusCode.OK) {
            val body = response.body<JsonObject>()
            body["id"]?.jsonPrimitive?.int
        } else {
            null
        }
    }

    override suspend fun createProject(
        name: String, 
        identifier: String,
        startDate: String?,
        endDate: String?,
        status: String?,
        parentId: Int?
    ): Int? {
        val payload = ProjectPayload(
            name = name, 
            identifier = identifier,
            startDate = startDate,
            endDate = endDate,
            links = ProjectLinksPayload(
                status = status?.let { Link(href = "/api/v3/project_statuses/$it") },
                parent = parentId?.let { Link(href = "/api/v3/projects/$it") }
            )
        )
        val response: HttpResponse = client.post("$host/api/v3/projects") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        return if ((response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK)) {
            val body = response.body<JsonObject>()
            body["id"]?.jsonPrimitive?.intOrNull
        } else {
            val errorBody = response.bodyAsText()
            println("      ⚠️ Project Creation Error ${response.status}: $errorBody")
            null
        }
    }

    override suspend fun updateProject(
        id: Int,
        startDate: String?,
        endDate: String?,
        status: String?,
        parentId: Int?
    ): Boolean {
        val payload = buildJsonObject {
            if (startDate != null) put("startDate", JsonPrimitive(startDate))
            if (endDate != null) put("endDate", JsonPrimitive(endDate))
            if (status != null || parentId != null) {
                put("_links", buildJsonObject {
                    if (status != null) {
                        put("status", buildJsonObject {
                            put("href", JsonPrimitive("/api/v3/project_statuses/$status"))
                        })
                    }
                    if (parentId != null) {
                        put("parent", buildJsonObject {
                            put("href", JsonPrimitive("/api/v3/projects/$parentId"))
                        })
                    }
                })
            }
        }
        
        val response: HttpResponse = client.patch("$host/api/v3/projects/$id") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(payload)
        }
        
        return if (response.status == HttpStatusCode.OK) {
            true
        } else {
            val errorBody = response.bodyAsText()
            println("      ⚠️ Project Update Error ${response.status}: $errorBody")
            false
        }
    }

    override suspend fun fetchWorkPackages(projectId: Int): List<WorkPackage> {
        val filters = buildJsonArray {
            add(buildJsonObject {
                put("project", buildJsonObject {
                    put("operator", "=")
                    putJsonArray("values") {
                        add(projectId.toString())
                    }
                })
            })
        }.toString()

        val response: HttpResponse = client.get("$host/api/v3/work_packages") {
            header(HttpHeaders.Authorization, authHeader)
            parameter("filters", filters)
            parameter("pageSize", 500)
        }

        return if (response.status == HttpStatusCode.OK) {
            val body = response.body<JsonObject>()
            val elements = body["_embedded"]?.jsonObject?.get("elements")?.jsonArray
            elements?.filterIsInstance<JsonObject>()?.map { wp: JsonObject ->
                WorkPackage(
                    id = wp["id"]?.jsonPrimitive?.content,
                    subject = wp["subject"]?.jsonPrimitive?.content ?: "",
                    projectId = projectId,
                    lockVersion = wp["lockVersion"]?.jsonPrimitive?.int,
                    percentageDone = wp["percentageDone"]?.jsonPrimitive?.intOrNull,
                    estimatedTime = wp["estimatedTime"]?.jsonPrimitive?.contentOrNull
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun createWorkPackage(workPackage: WorkPackage): Int? {
        val payload = WorkPackagePayload(
            subject = workPackage.subject,
            description = workPackage.description?.let { DescriptionPayload(raw = it) },
            startDate = workPackage.startDate,
            dueDate = workPackage.dueDate,
            estimatedTime = workPackage.estimatedTime,
            percentageDone = if ((workPackage.percentageDone ?: 0) > 0) workPackage.percentageDone else null,
            links = WorkPackageLinks(
                project = Link(href = "/api/v3/projects/${workPackage.projectId}"),
                type = workPackage.typeId?.let { Link(href = "/api/v3/types/$it") } 
                    ?: Link(href = "/api/v3/types/1"), // Default to Task
                status = workPackage.statusId?.let { Link(href = "/api/v3/statuses/$it") }
                    ?: Link(href = "/api/v3/statuses/1") // Default to New
            )
        )

        val response: HttpResponse = client.post("$host/api/v3/work_packages") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        return if (response.status == HttpStatusCode.Created) {
            val body = response.body<JsonObject>()
            body["id"]?.jsonPrimitive?.int
        } else {
            val errorBody = response.bodyAsText()
            println("      ⚠️ API Error ${response.status}: $errorBody")
            null
        }
    }

    override suspend fun updateWorkPackage(id: Int, workPackage: WorkPackage): Boolean {
        val payload = WorkPackagePayload(
            lockVersion = workPackage.lockVersion ?: 0,
            subject = workPackage.subject,
            description = workPackage.description?.let { DescriptionPayload(raw = it) },
            startDate = workPackage.startDate,
            dueDate = workPackage.dueDate,
            estimatedTime = workPackage.estimatedTime,
            percentageDone = workPackage.percentageDone,
            links = WorkPackageLinks(
                project = Link(href = "/api/v3/projects/${workPackage.projectId}"),
                type = workPackage.typeId?.let { Link(href = "/api/v3/types/$it") },
                status = workPackage.statusId?.let { Link(href = "/api/v3/statuses/$it") }
            )
        )

        val response: HttpResponse = client.patch("$host/api/v3/work_packages/$id") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        return if (response.status == HttpStatusCode.OK) {
            true
        } else {
            val errorBody = response.bodyAsText()
            println("      ⚠️ API Update Error ${response.status}: $errorBody")
            false
        }
    }
}
