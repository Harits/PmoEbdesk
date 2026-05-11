package com.sekota.pmoebdesk

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@Serializable
data class ProjectPayload(
    val name: String,
    val identifier: String,
    val description: DescriptionPayload = DescriptionPayload(raw = ""),
    val public: Boolean = false
)

@Serializable
data class DescriptionPayload(
    val format: String = "markdown",
    val raw: String
)

@Serializable
data class WorkPackagePayload(
    val lockVersion: Int? = null,
    val subject: String? = null,
    val description: DescriptionPayload? = null,
    val startDate: String? = null,
    val dueDate: String? = null,
    val _links: WorkPackageLinks? = null
)

@Serializable
data class WorkPackageLinks(
    val project: Link,
    val type: Link? = null,
    val status: Link? = null
)

@Serializable
data class Link(
    val href: String
)

class OpenProjectClient(val host: String, val apiKey: String) {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    private val authHeader = "Basic " + Base64.getEncoder().encodeToString("apikey:$apiKey".toByteArray())

    private var statusMap: Map<String, String>? = null
    private val wpCache = mutableMapOf<Int, MutableMap<String, JsonObject>>()

    suspend fun fetchStatuses() {
        val response: HttpResponse = client.get("$host/api/v3/statuses") {
            header(HttpHeaders.Authorization, authHeader)
        }
        if (response.status == HttpStatusCode.OK) {
            val body = response.body<JsonObject>()
            val embedded = body["_embedded"]?.jsonObject
            val elements = embedded?.get("elements")?.jsonArray
            statusMap = elements?.filterIsInstance<JsonObject>()?.associate { obj: JsonObject ->
                val name = obj["name"]!!.jsonPrimitive.content.lowercase()
                val href = obj["_links"]!!.jsonObject["self"]!!.jsonObject["href"]!!.jsonPrimitive.content
                name to href
            }
            println("Fetched statuses: ${statusMap?.keys}")
        } else {
            println("Failed to fetch statuses: ${response.status}")
        }
    }

    private fun getStatusHref(name: String): String? {
        return statusMap?.get(name.lowercase())
    }

    suspend fun getProject(identifier: String): Int? {
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

    suspend fun createProject(name: String, identifier: String): Int? {
        // First check if it exists
        val existingId = getProject(identifier)
        if (existingId != null) {
            fetchAllWorkPackages(existingId)
            return existingId
        }

        val payload = ProjectPayload(name, identifier)
        val response: HttpResponse = client.post("$host/api/v3/projects") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        return if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
            val body = response.body<JsonObject>()
            val newId = body["id"]?.jsonPrimitive?.intOrNull
            if (newId != null) {
                fetchAllWorkPackages(newId)
            }
            newId
        } else {
            println("Failed to create project $name: ${response.status} - ${response.bodyAsText()}")
            // If project exists, we might want to fetch it instead. For now, return null.
            null
        }
    }

    suspend fun fetchAllWorkPackages(projectId: Int) {
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

        if (response.status == HttpStatusCode.OK) {
            val body = response.body<JsonObject>()
            val total = body["total"]?.jsonPrimitive?.int ?: 0
            val elements = body["_embedded"]?.jsonObject?.get("elements")?.jsonArray
            val projectCache = wpCache.getOrPut(projectId) { mutableMapOf<String, JsonObject>() }
            elements?.filterIsInstance<JsonObject>()?.forEach { wp: JsonObject ->
                val subject = wp["subject"]?.jsonPrimitive?.content ?: return@forEach
                projectCache[subject] = wp
            }
            println("Fetched ${elements?.size ?: 0} of $total work packages for project $projectId")
        } else {
            println("Failed to fetch work packages for project $projectId: ${response.status}")
        }
    }

    suspend fun createWorkPackage(
        projectId: Int,
        subject: String,
        description: String,
        startDate: String?,
        dueDate: String?,
        statusName: String? = null,
        dryRun: Boolean = false
    ): Int? {
        // First check the cache
        val existing = wpCache[projectId]?.get(subject)
        if (existing != null) {
            val wpId = existing["id"]?.jsonPrimitive?.int ?: return null
            val lockVersion = existing["lockVersion"]?.jsonPrimitive?.int ?: return null
            val currentStatusHref = existing["_links"]?.jsonObject?.get("status")?.jsonObject?.get("href")?.jsonPrimitive?.content
            val targetStatusHref = statusName?.let { getStatusHref(it) }

            if (currentStatusHref != targetStatusHref) {
                if (dryRun) {
                    println("[DRY RUN] Would update status for work package $subject ($wpId) to $statusName")
                } else {
                    println("Updating status for work package $subject ($wpId) to $statusName")
                    updateWorkPackage(wpId, lockVersion, statusName)
                    // Update cache after successful update if needed, but status update doesn't change ID or subject
                }
            } else {
                println("Work package $subject already exists and is up to date.")
            }
            return wpId
        }

        if (dryRun) {
            println("[DRY RUN] Would create work package: $subject")
            return -1
        }

        val statusHref = statusName?.let { getStatusHref(it) }
        val payload = WorkPackagePayload(
            subject = subject,
            description = DescriptionPayload(raw = description),
            startDate = startDate,
            dueDate = dueDate,
            _links = WorkPackageLinks(
                project = Link(href = "/api/v3/projects/$projectId"),
                status = statusHref?.let { Link(href = it) }
            )
        )

        val response: HttpResponse = client.post("$host/api/v3/work_packages") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(payload)
        }

        return if (response.status == HttpStatusCode.Created) {
            val body = response.body<JsonObject>()
            val newWp = body
            val wpId = newWp["id"]?.jsonPrimitive?.int
            if (wpId != null) {
                wpCache.getOrPut(projectId) { mutableMapOf() }[subject] = newWp
            }
            wpId
        } else {
            println("Failed to create work package $subject: ${response.status} - ${response.bodyAsText()}")
            null
        }
    }

    suspend fun updateWorkPackage(id: Int, lockVersion: Int, statusName: String?): Boolean {
        val statusHref = statusName?.let { getStatusHref(it) }
        
        val patchBody = buildJsonObject {
            put("lockVersion", lockVersion)
            if (statusHref != null) {
                put("_links", buildJsonObject {
                    put("status", buildJsonObject {
                        put("href", statusHref)
                    })
                })
            }
        }

        val response: HttpResponse = client.patch("$host/api/v3/work_packages/$id") {
            header(HttpHeaders.Authorization, authHeader)
            contentType(ContentType.Application.Json)
            setBody(patchBody)
        }

        return if (response.status == HttpStatusCode.OK) {
            true
        } else {
            println("Failed to update work package $id: ${response.status} - ${response.bodyAsText()}")
            false
        }
    }
    
    fun close() {
        client.close()
    }
}

data class ExpandedTask(
    val subject: String,
    val startDate: String?,
    val dueDate: String?,
    val status: String // "New" or "Closed"
)

fun expandTask(subject: String, startDate: String?, finishDate: String?, referenceDate: LocalDate = LocalDate.now()): List<ExpandedTask> {
    if (startDate == null || finishDate == null) return listOf(ExpandedTask(subject, startDate, finishDate, "New"))

    val start = try { LocalDate.parse(startDate) } catch (e: Exception) { null } ?: return listOf(ExpandedTask(subject, startDate, finishDate, "New"))
    val end = try { LocalDate.parse(finishDate) } catch (e: Exception) { null } ?: return listOf(ExpandedTask(subject, startDate, finishDate, "New"))

    return if (subject.contains("Monthly", ignoreCase = true)) {
        val tasks = mutableListOf<ExpandedTask>()
        var current = start.withDayOfMonth(1)
        val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)

        while (!current.isAfter(end)) {
            val taskStart = if (current.isBefore(start)) start else current
            val endOfMonth = current.withDayOfMonth(current.lengthOfMonth())
            val taskEnd = if (endOfMonth.isAfter(end)) end else endOfMonth

            if (!taskEnd.isBefore(taskStart)) {
                // Determine status: If the month of this task is strictly before the reference month, it's Closed.
                // Otherwise, it's New.
                val status = if (current.withDayOfMonth(1).isBefore(referenceDate.withDayOfMonth(1))) {
                    "Closed"
                } else {
                    "New"
                }

                tasks.add(ExpandedTask(
                    subject = "$subject - ${current.format(monthFormatter)}",
                    startDate = taskStart.toString(),
                    dueDate = taskEnd.toString(),
                    status = status
                ))
            }
            current = current.plusMonths(1)
        }
        tasks
    } else {
        val status = if (end.isBefore(referenceDate)) "Closed" else "New"
        listOf(ExpandedTask(subject, startDate, finishDate, status))
    }
}

fun loadEnv(path: String): Map<String, String> {
    val file = File(path)
    if (!file.exists()) return emptyMap()
    return file.readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .associate {
            val parts = it.split("=", limit = 2)
            parts[0].trim() to parts[1].trim()
        }
}

fun slugify(text: String): String {
    return text.lowercase()
        .replace(Regex("[^a-z0-9]"), "-")
        .replace(Regex("-+"), "-")
        .trim('-')
}

// Convert "9-Apr-2025" to "2025-04-09"
fun formatDate(dateStr: String?): String? {
    if (dateStr.isNullOrBlank()) return null
    try {
        val parts = dateStr.split("-")
        if (parts.size != 3) return null
        val day = parts[0].padStart(2, '0')
        val month = when (parts[1].lowercase()) {
            "jan" -> "01"
            "feb" -> "02"
            "mar" -> "03"
            "apr" -> "04"
            "may" -> "05"
            "jun" -> "06"
            "jul" -> "07"
            "aug" -> "08"
            "sep" -> "09"
            "oct" -> "10"
            "nov" -> "11"
            "dec" -> "12"
            else -> return null
        }
        val year = parts[2]
        return "$year-$month-$day"
    } catch (e: Exception) {
        return null
    }
}

fun main() = runBlocking {
    // Try to find the root directory by looking for "gradlew"
    var rootDir = File("").absoluteFile
    while (rootDir != null && !File(rootDir, "gradlew").exists()) {
        rootDir = rootDir.parentFile
    }

    if (rootDir == null) {
        println("Could not find project root (gradlew)")
        return@runBlocking
    }

    val envFile = File(rootDir, ".openproject/.env")
    println("Loading env from: ${envFile.absolutePath}")
    val env = loadEnv(envFile.absolutePath)
    val host = env["OPENPROJECT_HOST"] ?: "http://localhost:8080"
    val apiKey = env["OPENPROJECT_API_KEY"] ?: ""

    if (apiKey.isBlank()) {
        println("API Key not found in ${envFile.absolutePath}")
        return@runBlocking
    }

    val client = OpenProjectClient(host, apiKey)
    client.fetchStatuses()
    
    val csvFile = File(rootDir, "data/ISO_PMO_ ET_2025.csv")
    
    if (!csvFile.exists()) {
        println("CSV file not found: ${csvFile.absolutePath}")
        return@runBlocking
    }

    val dryRun = false // Set to false for actual sync
    if (dryRun) println("=== DRY RUN MODE ENABLED ===")

    csvReader {
        autoRenameDuplicateHeaders = true
    }.readAllWithHeader(csvFile).forEach { row ->
        val projectName = row["Nama Projek"] ?: return@forEach
        val projectIdentifier = slugify(projectName)
        val customer = row["CUSTOMER"] ?: ""
        val startDate = formatDate(row["Start"])
        val finishDate = formatDate(row["Finish"])
        val task1 = row["Task 1"] ?: ""
        val task2 = row["Task 2"] ?: ""

        println("Processing Project: $projectName ($projectIdentifier)")
        
        val projectId = client.createProject(projectName, projectIdentifier)
        if (projectId != null) {
            if (projectId != -1) println("Project ID: $projectId")
            
            listOf(task1, task2).filter { it.isNotBlank() }.forEach { taskSubject ->
                expandTask(taskSubject, startDate, finishDate).forEach { expanded ->
                    val wpId = client.createWorkPackage(
                        projectId, 
                        expanded.subject, 
                        "Customer: $customer", 
                        expanded.startDate, 
                        expanded.dueDate,
                        expanded.status,
                        dryRun = dryRun
                    )
                    if (wpId != null && wpId != -1) {
                        println("Sync/Found Task: ${expanded.subject} (ID: $wpId)")
                    }
                }
            }
        } else {
            println("Skipping work packages for $projectName due to project creation failure.")
        }
    }
    
    client.close()
    println("Sync completed.")
}
