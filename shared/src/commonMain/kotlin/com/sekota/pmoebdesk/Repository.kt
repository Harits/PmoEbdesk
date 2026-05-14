package com.sekota.pmoebdesk

interface OpenProjectRepository {
    suspend fun getDashboardMetrics(baseUrl: String, apiKey: String): DashboardMetrics
}
