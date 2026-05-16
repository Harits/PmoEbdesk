package com.sekota.pmoebdesk.features.dashboard.domain

interface OpenProjectRepository {
    suspend fun getDashboardMetrics(baseUrl: String, apiKey: String): DashboardMetrics
}
