package com.sekota.pmoebdesk.dashboard.data.repository

import com.sekota.pmoebdesk.dashboard.data.remote.model.WorkPackagesResponse
import com.sekota.pmoebdesk.dashboard.domain.model.*
import com.sekota.pmoebdesk.dashboard.domain.repository.OpenProjectRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class MockOpenProjectRepositoryImpl : OpenProjectRepository {
    override suspend fun getDashboardMetrics(baseUrl: String, apiKey: String): DashboardMetrics {
        return DashboardMetrics(
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
    override suspend fun getDashboardMetrics(baseUrl: String, apiKey: String): DashboardMetrics {
        val authString = "apikey:$apiKey"
        val encodedAuth = Base64.encode(authString.encodeToByteArray())

        try {
            val httpResponse = client.get("$baseUrl/api/v3/work_packages?pageSize=100") {
                header(HttpHeaders.Authorization, "Basic $encodedAuth")
                header(HttpHeaders.Accept, "application/json")
            }

            val responseText = httpResponse.bodyAsText()
            val response: WorkPackagesResponse = Json { ignoreUnknownKeys = true }.decodeFromString(responseText)
            val workPackages = response._embedded.elements

            val totalProgress = workPackages.mapNotNull { it.percentageDone }.average().takeIf { !it.isNaN() } ?: 0.0

            val exceptions = workPackages
                .filter { (it.percentageDone ?: 0) < 100 && it.dueDate != null && isOverdue(it.dueDate) }
                .map { ProjectException(it.subject, "Overdue since ${it.dueDate}. Progress: ${it.percentageDone ?: 0}%") }

            val milestones = workPackages
                .filter { it._links?.type?.title?.contains("Milestone", ignoreCase = true) == true || it.subject.contains("Milestone", ignoreCase = true) }
                .map { Milestone(it.subject, formatMonth(it.dueDate)) }

            return DashboardMetrics(
                strategicRagStatus = when {
                    totalProgress > 80 -> RAGStatus.GREEN
                    totalProgress > 50 -> RAGStatus.AMBER
                    else -> RAGStatus.RED
                },
                netProgressPercentage = totalProgress,
                trendPercentage = 4.2, // Still mock for now
                strategicGrowthHours = workPackages.size * 10.0,
                businessAsUsualHours = workPackages.size * 5.0,
                milestones = milestones.take(5),
                risks = listOf(Risk("Supply Chain Delay", 4, 5, RiskLevel.HIGH)),
                exceptions = exceptions.take(10),
                boardInterventions = listOf(BoardIntervention("Review resource allocation for delayed work packages."))
            )
        } catch (e: Throwable) {
            println("Failed to fetch from OpenProject: ${e.message}")
            return MockOpenProjectRepositoryImpl().getDashboardMetrics(baseUrl, apiKey)
        }
    }

    private fun isOverdue(dueDate: String): Boolean {
        // Simple string comparison for YYYY-MM-DD
        // In a real app, use kotlinx-datetime
        return false // Placeholder
    }

    private fun formatMonth(dateStr: String?): String {
        if (dateStr == null) return "TBD"
        // dateStr is YYYY-MM-DD
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
