package com.sekota.pmoebdesk.projects.data.repository

import com.sekota.pmoebdesk.dashboard.data.remote.model.WorkPackagesResponse
import com.sekota.pmoebdesk.projects.data.remote.model.ProjectElement
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
            Project(3, "project-c", "Project Gamma", ProjectStatus.OFF_TRACK, "Rp 750M", "2025-09-15", "2024-02-10", 8, isWarning = true),
            Project(4, "project-d", "Internal Tools", ProjectStatus.FINISHED, "Rp 200M", "2024-12-01", "2024-01-10", 3),
            Project(5, "project-delta", "Project Delta", ProjectStatus.ON_TRACK, "Rp 2.5B", "2025-12-31", "2024-05-01", 15),
            Project(6, "project-e", "Project E", ProjectStatus.NOT_STARTED, "Rp 100M", "2026-01-01", "2025-01-01", 0),
            Project(7, "project-f", "Project F", ProjectStatus.DISCONTINUED, "Rp 50M", "2024-01-01", "2023-01-01", 2)
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
                val elements = projectsResponse._embedded.elements
                
                // Fetch dates from work packages for each project
                val projectDates = if (elements.isNotEmpty()) {
                    fetchProjectDates(elements.map { it.id })
                } else emptyMap()

                val allProjects = elements.map { element ->
                    val projectStatus = mapStatus(element)
                    val (wpStart, wpEnd) = projectDates[element.id] ?: (null to null)
                    
                    val start = wpStart ?: element.startDate
                    val end = wpEnd ?: element.endDate

                    if (start == null || end == null) {
                        println("      ⚠️ Project '${element.name}' (ID: ${element.id}) is missing dates in OpenProject.")
                    }

                    Project(
                        id = element.id,
                        identifier = element.identifier,
                        name = element.name,
                        status = projectStatus,
                        budget = "TBD", // Budget mapping might need custom fields
                        deadline = end ?: "TBD",
                        startedDate = start ?: "TBD",
                        teamCount = 0,
                        isWarning = projectStatus == ProjectStatus.AT_RISK || projectStatus == ProjectStatus.OFF_TRACK
                    )
                }

                // Filter locally for query and status for better reliability
                return allProjects.filter { p ->
                    (query.isNullOrBlank() || p.name.contains(query, ignoreCase = true) || p.identifier.contains(query, ignoreCase = true)) &&
                    (status == null || p.status == status)
                }
            }
        } catch (e: Exception) {
            println("Error searching projects: ${e.message}")
        }
        return emptyList()
    }

    private suspend fun fetchProjectDates(projectIds: List<Int>): Map<Int, Pair<String?, String?>> {
        try {
            val filters = buildJsonArray {
                add(buildJsonObject {
                    put("project", buildJsonObject {
                        put("operator", "=")
                        putJsonArray("values") {
                            projectIds.forEach { add(it.toString()) }
                        }
                    })
                })
            }.toString()

            val response: HttpResponse = client.get("$host/api/v3/work_packages") {
                header(HttpHeaders.Authorization, authHeader)
                parameter("filters", filters)
                parameter("pageSize", 1000)
                parameter("select", "id,subject,startDate,dueDate,_links")
            }

            if (response.status == HttpStatusCode.OK) {
                val bodyText = response.bodyAsText()
                val wpResponse = Json { ignoreUnknownKeys = true }.decodeFromString<WorkPackagesResponse>(bodyText)
                val workPackages = wpResponse._embedded.elements

                return workPackages.groupBy { wp ->
                    // Extract project ID from href: /api/v3/projects/123
                    wp._links?.project?.href?.split("/")?.lastOrNull()?.toIntOrNull()
                }.mapNotNull { (projectId, wps) ->
                    if (projectId == null) return@mapNotNull null
                    val start = wps.mapNotNull { it.startDate }.minOrNull()
                    val end = wps.mapNotNull { it.dueDate }.maxOrNull()
                    projectId to (start to end)
                }.toMap()
            }
        } catch (e: Exception) {
            println("Error fetching project dates: ${e.message}")
        }
        return emptyMap()
    }

    private fun mapStatus(element: ProjectElement): ProjectStatus {
        val statusName = element.status ?: element._links?.status?.title
        return when (statusName?.lowercase()?.replace("_", " ")) {
            "on track", "green", "on_track" -> ProjectStatus.ON_TRACK
            "at risk", "amber", "at_risk" -> ProjectStatus.AT_RISK
            "off track", "red", "off_track", "critical" -> ProjectStatus.OFF_TRACK
            "finished", "completed", "closed" -> ProjectStatus.FINISHED
            "on hold", "on_hold" -> ProjectStatus.ON_HOLD
            "not started", "not_started" -> ProjectStatus.NOT_STARTED
            "discontinued" -> ProjectStatus.DISCONTINUED
            "not set", "not_set" -> ProjectStatus.NOT_SET
            else -> ProjectStatus.UNKNOWN
        }
    }
}
