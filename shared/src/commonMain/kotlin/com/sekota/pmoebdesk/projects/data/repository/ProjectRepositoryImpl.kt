package com.sekota.pmoebdesk.projects.data.repository

import com.sekota.pmoebdesk.projects.data.remote.model.ProjectsResponse
import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus
import com.sekota.pmoebdesk.projects.domain.repository.ProjectRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class MockProjectRepositoryImpl : ProjectRepository {
    override suspend fun searchProjects(query: String?, parentId: Int?, allowedIds: List<Int>?, status: ProjectStatus?): List<Project> {
        val allProjects = listOf(
            Project(1, "project-a", "Project Alpha", ProjectStatus.ON_TRACK, "Rp 500M", "2025-12-31", "2024-01-01", 5),
            Project(2, "project-b", "Project Beta", ProjectStatus.AT_RISK, "Rp 1.2B", "2025-06-30", "2024-03-15", 12, isWarning = true),
            Project(3, "project-c", "Project Gamma", ProjectStatus.CRITICAL, "Rp 750M", "2025-09-15", "2024-02-10", 8, isWarning = true),
            Project(4, "project-d", "Internal Tools", ProjectStatus.COMPLETED, "Rp 200M", "2024-12-01", "2024-01-10", 3)
        )
        
        return allProjects.filter { p ->
            (query.isNullOrBlank() || p.name.contains(query, ignoreCase = true) || p.identifier.contains(query, ignoreCase = true)) &&
            (allowedIds == null || allowedIds.contains(p.id)) &&
            (status == null || p.status == status)
        }
    }
}

class ProductionProjectRepositoryImpl(
    private val client: HttpClient,
    private val host: String,
    private val apiKey: String
) : ProjectRepository {

    @OptIn(ExperimentalEncodingApi::class)
    private val authHeader = "Basic " + Base64.encode("apikey:$apiKey".encodeToByteArray())

    override suspend fun searchProjects(query: String?, parentId: Int?, allowedIds: List<Int>?, status: ProjectStatus?): List<Project> {
        try {
            val filters = buildJsonArray {
                if (!query.isNullOrBlank()) {
                    add(buildJsonObject {
                        put("name", buildJsonObject {
                            put("operator", "**")
                            putJsonArray("values") { add(query) }
                        })
                    })
                }
                if (parentId != null) {
                    add(buildJsonObject {
                        put("parent", buildJsonObject {
                            put("operator", "=")
                            putJsonArray("values") { add(parentId.toString()) }
                        })
                    })
                }
                if (!allowedIds.isNullOrEmpty()) {
                    add(buildJsonObject {
                        put("id", buildJsonObject {
                            put("operator", "=")
                            putJsonArray("values") { 
                                allowedIds.forEach { add(it.toString()) }
                            }
                        })
                    })
                }
                if (status != null) {
                    add(buildJsonObject {
                        put("status", buildJsonObject {
                            put("operator", "=")
                            putJsonArray("values") { 
                                add(mapProjectStatusToOpenProject(status))
                            }
                        })
                    })
                }
            }

            val response: HttpResponse = client.get("$host/api/v3/projects") {
                header(HttpHeaders.Authorization, authHeader)
                if (filters.isNotEmpty()) {
                    parameter("filters", filters.toString())
                }
                parameter("pageSize", 100)
            }

            if (response.status == HttpStatusCode.OK) {
                val bodyText = response.bodyAsText()
                val projectsResponse = Json { ignoreUnknownKeys = true }.decodeFromString<ProjectsResponse>(bodyText)
                return projectsResponse._embedded.elements.map { element ->
                    Project(
                        id = element.id,
                        identifier = element.identifier,
                        name = element.name,
                        status = mapStatus(element.status),
                        budget = "TBD", // Budget mapping might need custom fields
                        deadline = "TBD",
                        startedDate = "TBD",
                        teamCount = 0
                    )
                }
            }
        } catch (e: Exception) {
            println("Error searching projects: ${e.message}")
        }
        return emptyList()
    }

    private fun mapStatus(status: String?): ProjectStatus {
        return when (status?.lowercase()) {
            "on track", "green" -> ProjectStatus.ON_TRACK
            "at risk", "amber" -> ProjectStatus.AT_RISK
            "critical", "red" -> ProjectStatus.CRITICAL
            "completed" -> ProjectStatus.COMPLETED
            "on hold" -> ProjectStatus.ON_HOLD
            else -> ProjectStatus.UNKNOWN
        }
    }

    private fun mapProjectStatusToOpenProject(status: ProjectStatus): String {
        return when (status) {
            ProjectStatus.ON_TRACK -> "on track"
            ProjectStatus.AT_RISK -> "at risk"
            ProjectStatus.CRITICAL -> "critical"
            ProjectStatus.COMPLETED -> "completed"
            ProjectStatus.ON_HOLD -> "on hold"
            ProjectStatus.UNKNOWN -> ""
        }
    }
}
