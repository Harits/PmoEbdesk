package com.sekota.pmoebdesk.dashboard.domain.repository

import com.sekota.pmoebdesk.dashboard.domain.model.DashboardMetrics

interface OpenProjectRepository {
    suspend fun getDashboardMetrics(baseUrl: String, apiKey: String): DashboardMetrics
}
