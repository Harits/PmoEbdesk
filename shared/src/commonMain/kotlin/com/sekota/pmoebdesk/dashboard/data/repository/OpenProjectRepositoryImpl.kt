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
            strategicGrowthHours = 600.0,
            businessAsUsualHours = 400.0,
            milestones = listOf(
                Milestone("Product Launch", "Oct"),
                Milestone("Market Entry", "Nov"),
                Milestone("Q3 Audit", "Dec")
            ),
            risks = listOf(
                Risk("Supply Chain Delay", 4, 5),
                Risk("Key Personnel Departure", 3, 4),
                Risk("Budget Overrun", 2, 4)
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
            println("Fetching dashboard metrics from $baseUrl/api/v3/work_packages...")
            
            val httpResponse = client.get("$baseUrl/api/v3/work_packages?pageSize=20") {
                header(HttpHeaders.Authorization, "Basic $encodedAuth")
                header(HttpHeaders.Accept, "application/json")
            }
            
            println("HTTP Response received: ${httpResponse.status}")
            
            val responseText = httpResponse.bodyAsText()
            println("Response body length: ${responseText.length}")

            val response: WorkPackagesResponse = Json { ignoreUnknownKeys = true }.decodeFromString(responseText)
            println("Decoded ${response.count} work packages")

            val workPackages = response._embedded.elements

            // Basic aggregation logic based on raw data
            val totalProgress = workPackages.mapNotNull { it.percentageDone }.average().takeIf { !it.isNaN() } ?: 0.0

            // Dummy logic to map real work packages to domain elements (to be refined later)
            val exceptions = workPackages.take(2).map {
                ProjectException(it.subject, "Off track: ${it.percentageDone}% complete")
            }

            val milestones = workPackages.takeLast(3).map {
                Milestone(it.subject, "TBD")
            }

            return DashboardMetrics(
                strategicRagStatus = if (totalProgress > 80) RAGStatus.GREEN else if (totalProgress > 50) RAGStatus.AMBER else RAGStatus.RED,
                netProgressPercentage = totalProgress,
                strategicGrowthHours = workPackages.size * 10.0, // Mock calculation
                businessAsUsualHours = workPackages.size * 5.0,  // Mock calculation
                milestones = milestones,
                risks = listOf(
                    Risk("Supply Chain Delay", 4, 5) // Example hardcoded risk
                ),
                exceptions = exceptions,
                boardInterventions = listOf(
                    BoardIntervention("Review resource allocation for delayed work packages.")
                )
            )
        } catch (e: Throwable) {
            println("Failed to fetch from OpenProject: ${e.message}")
            e.printStackTrace()
            // Fallback to mock on error
            return MockOpenProjectRepositoryImpl().getDashboardMetrics(baseUrl, apiKey)
        }
    }
}
