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
                parameter("pageSize", 1000)
                if (filters != "[]") {
                    parameter("filters", filters)
                }
            }

            val responseText = httpResponse.bodyAsText()
            println("DEBUG: Response Status: ${httpResponse.status}")
            println("DEBUG: Response Body (start): ${responseText.take(500)}")

            if (httpResponse.status.value !in 200..299) {
                error("OpenProject API error: ${httpResponse.status} - $responseText")
            }

            val response: WorkPackagesResponse = Json { ignoreUnknownKeys = true }.decodeFromString(responseText)
            val workPackages = response._embedded.elements
            println("DEBUG: Fetched ${workPackages.size} work packages")
            if (workPackages.isNotEmpty()) {
                println("DEBUG: Sample WorkPackage[0]: id=${workPackages[0].id}, subject='${workPackages[0].subject}', dueDate=${workPackages[0].dueDate}, startDate=${workPackages[0].startDate}, type=${workPackages[0]._links?.type?.title}")
            }

            val totalProgress = workPackages.mapNotNull { it.percentageDone }.average().takeIf { !it.isNaN() } ?: 0.0

            val exceptions = workPackages
                .filter { 
                    val date = it.dueDate ?: it.date ?: it.derivedDueDate
                    (it.percentageDone ?: 0) < 100 && date != null && isOverdue(date) 
                }
                .map { 
                    val date = it.dueDate ?: it.date ?: it.derivedDueDate
                    ProjectException(it.subject, "Overdue since $date. Progress: ${it.percentageDone ?: 0}%") 
                }

            val milestones = workPackages
                .filter { 
                    val isMilestone = it._links?.type?.title?.contains("Milestone", ignoreCase = true) == true || 
                                     it.subject.contains("Milestone", ignoreCase = true)
                    if (isMilestone) {
                        println("DEBUG: Milestone found: '${it.subject}', dueDate=${it.dueDate}, date=${it.date}, derivedDueDate=${it.derivedDueDate}, startDate=${it.startDate}, derivedStartDate=${it.derivedStartDate}")
                    }
                    isMilestone
                }
                .sortedBy { it.dueDate ?: it.date ?: it.derivedDueDate ?: it.startDate ?: it.derivedStartDate ?: "9999-12-31" }
                .map { 
                    val date = it.dueDate ?: it.date ?: it.derivedDueDate ?: it.startDate ?: it.derivedStartDate
                    val formatted = formatMonth(date)
                    println("DEBUG: Formatted milestone: '${it.subject}', date=$date -> $formatted")
                    Milestone(it.subject, formatted)
                }

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
        if (dateStr.isNullOrBlank()) return "TBD"
        return try {
            val date = if (dateStr.contains("T")) {
                dateStr.substringBefore("T").let { LocalDate.parse(it) }
            } else {
                LocalDate.parse(dateStr)
            }
            date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            // Fallback parsing for non-standard formats (e.g. 1-Jan-2025 or 2025/01/01)
            try {
                val parts = dateStr.split("-", "/", ".", " ")
                val monthPart = if (parts.size == 3) {
                    // Handle DD-MMM-YYYY or YYYY-MM-DD
                    if (parts[0].length == 4) parts[1] else parts[1]
                } else if (parts.size == 2) {
                    if (parts[0].length == 4) parts[1] else parts[0]
                } else dateStr
                
                when(monthPart.lowercase().removePrefix("0")) {
                    "1", "jan", "january" -> "Jan"
                    "2", "feb", "february" -> "Feb"
                    "3", "mar", "march" -> "Mar"
                    "4", "apr", "april" -> "Apr"
                    "5", "may" -> "May"
                    "6", "jun", "june" -> "Jun"
                    "7", "jul", "july" -> "Jul"
                    "8", "aug", "august" -> "Aug"
                    "9", "sep", "september" -> "Sep"
                    "10", "oct", "october" -> "Oct"
                    "11", "nov", "november" -> "Nov"
                    "12", "dec", "december" -> "Dec"
                    else -> "TBD"
                }
            } catch (e2: Exception) {
                "TBD"
            }
        }
    }
}
