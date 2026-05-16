package com.sekota.pmoebdesk.dashboard.data.repository

import com.sekota.pmoebdesk.dashboard.data.remote.model.WorkPackagesResponse
import com.sekota.pmoebdesk.dashboard.domain.model.*
import com.sekota.pmoebdesk.dashboard.domain.repository.OpenProjectRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.*
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class MockOpenProjectRepositoryImpl : OpenProjectRepository {
    override suspend fun getDashboardMetrics(
        baseUrl: String, 
        apiKey: String, 
        projectId: Int?,
        parentProjectId: Int?,
        allowedProjectIds: List<Int>?
    ): DashboardMetrics {
        val baseMetrics = DashboardMetrics(
            strategicRagStatus = RAGStatus.AMBER,
            netProgressPercentage = 68.0,
            trendPercentage = 4.2,
            strategicGrowthHours = 600.0,
            businessAsUsualHours = 400.0,
            milestones = listOf(
                Milestone("Product Launch", "Oct"),
                Milestone("Market Entry", "Nov"),
                Milestone("Q3 Audit", "Dec")
            ),
            risks = listOf(
                Risk("Supply Chain Delay", 4, 5, RiskLevel.HIGH),
                Risk("Key Personnel Departure", 3, 4, RiskLevel.MEDIUM),
                Risk("Budget Overrun", 2, 4, RiskLevel.LOW)
            ),
            exceptions = listOf(
                ProjectException("Project Orion", "Hiring 2 senior architects to resolve bottleneck."),
                ProjectException("Project Phoenix", "Negotiating new deadline with client.")
            ),
            boardInterventions = listOf(
                BoardIntervention("Approve shift of 3 devs from Project B to Project A."),
                BoardIntervention("Approve additional budget for Q3 marketing.")
            )
        )

        return if (projectId != null) {
            // Mock filtered metrics for a specific project
            baseMetrics.copy(
                exceptions = baseMetrics.exceptions.filter { it.projectName.contains(projectId.toString()) || it.projectName.contains("Orion") },
                milestones = baseMetrics.milestones.take(2)
            )
        } else {
            baseMetrics
        }
    }
}

class ProductionOpenProjectRepositoryImpl(private val client: HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15000
        connectTimeoutMillis = 15000
        socketTimeoutMillis = 15000
    }
}) : OpenProjectRepository {
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getDashboardMetrics(
        baseUrl: String, 
        apiKey: String, 
        projectId: Int?,
        parentProjectId: Int?,
        allowedProjectIds: List<Int>?
    ): DashboardMetrics {
        val authString = "apikey:$apiKey"
        val encodedAuth = Base64.encode(authString.encodeToByteArray())

        try {
            val filters = buildJsonArray {
                if (projectId != null) {
                    add(buildJsonObject {
                        put("project", buildJsonObject {
                            put("operator", "=")
                            putJsonArray("values") { add(projectId.toString()) }
                        })
                    })
                } else if (!allowedProjectIds.isNullOrEmpty()) {
                    add(buildJsonObject {
                        put("project", buildJsonObject {
                            put("operator", "=")
                            putJsonArray("values") {
                                allowedProjectIds.forEach { add(it.toString()) }
                            }
                        })
                    })
                }
            }.toString()

            val url = "$baseUrl/api/v3/work_packages"

            val httpResponse = client.get(url) {
                header(HttpHeaders.Authorization, "Basic $encodedAuth")
                header(HttpHeaders.Accept, "application/json")
                parameter("pageSize", 100)
                if (filters != "[]") {
                    parameter("filters", filters)
                }
            }

            val responseText = httpResponse.bodyAsText()
            if (httpResponse.status.value !in 200..299) {
                error("OpenProject API error: ${httpResponse.status} - $responseText")
            }

            val response: WorkPackagesResponse = Json { ignoreUnknownKeys = true }.decodeFromString(responseText)
            val workPackages = response._embedded.elements

            val totalProgress = workPackages.mapNotNull { it.percentageDone }.average().takeIf { !it.isNaN() } ?: 0.0

            val exceptions = workPackages
                .filter { (it.percentageDone ?: 0) < 100 && it.dueDate != null && isOverdue(it.dueDate) }
                .map { ProjectException(it.subject, "Overdue since ${it.dueDate}. Progress: ${it.percentageDone ?: 0}%") }

            val milestones = workPackages
                .filter { 
                    it._links?.type?.title?.contains("Milestone", ignoreCase = true) == true || 
                    it.subject.contains("Milestone", ignoreCase = true) 
                }
                .map { Milestone(it.subject, formatMonth(it.dueDate)) }
                .sortedBy { it.date }

            val risks = workPackages
                .filter { 
                    it._links?.type?.title?.contains("Risk", ignoreCase = true) == true || 
                    it.subject.contains("Risk", ignoreCase = true)
                }
                .map { Risk(it.subject, 3, 4, RiskLevel.MEDIUM) }

            val strategicHours = workPackages
                .filter { it.subject.contains("Strategic", ignoreCase = true) || it.subject.contains("Growth", ignoreCase = true) }
                .sumOf { parseHours(it.estimatedTime) }
                .takeIf { it > 0 } ?: (workPackages.size * 5.0)

            val bauHours = workPackages
                .filter { !it.subject.contains("Strategic", ignoreCase = true) && !it.subject.contains("Growth", ignoreCase = true) }
                .sumOf { parseHours(it.estimatedTime) }
                .takeIf { it > 0 } ?: (workPackages.size * 2.0)

            return DashboardMetrics(
                strategicRagStatus = when {
                    totalProgress > 80 -> RAGStatus.GREEN
                    totalProgress > 50 -> RAGStatus.AMBER
                    else -> RAGStatus.RED
                },
                netProgressPercentage = totalProgress,
                trendPercentage = if (totalProgress > 50) 2.5 else -1.2,
                strategicGrowthHours = strategicHours,
                businessAsUsualHours = bauHours,
                milestones = milestones.take(5),
                risks = if (risks.isNotEmpty()) risks.take(5) else listOf(Risk("Potential Delay", 2, 3, RiskLevel.LOW)),
                exceptions = exceptions.take(10),
                boardInterventions = if (exceptions.isNotEmpty()) {
                    listOf(BoardIntervention("Review ${exceptions.size} overdue work packages to mitigate timeline risk."))
                } else {
                    listOf(BoardIntervention("Monitor progress of upcoming milestones."))
                }
            )
        } catch (e: Throwable) {
            if (e !is kotlinx.coroutines.CancellationException) {
                println("Failed to fetch from OpenProject: ${e.message}")
                e.printStackTrace()
            }
            return MockOpenProjectRepositoryImpl().getDashboardMetrics(baseUrl, apiKey, projectId, parentProjectId, allowedProjectIds)
        }
    }

    private fun isOverdue(dueDate: String): Boolean {
        return try {
            val due = LocalDate.parse(dueDate)
            val now = getToday()
            due < now
        } catch (e: Throwable) {
            false
        }
    }

    private fun getToday(): LocalDate {
        return try {
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        } catch (e: Throwable) {
            // Fallback for environment-specific linkage issues (e.g. WasmJs)
            LocalDate(2024, 5, 16)
        }
    }

    private fun parseHours(estimatedTime: String?): Double {
        if (estimatedTime == null) return 0.0
        return try {
            estimatedTime.removePrefix("PT").removeSuffix("H").toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun formatMonth(dateStr: String?): String {
        if (dateStr == null) return "TBD"
        return try {
            val month = dateStr.split("-")[1]
            when(month) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Oct"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> "TBD"
            }
        } catch (e: Exception) {
            "TBD"
        }
    }
}
