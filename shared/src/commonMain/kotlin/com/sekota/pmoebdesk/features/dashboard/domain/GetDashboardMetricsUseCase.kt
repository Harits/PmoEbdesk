package com.sekota.pmoebdesk.features.dashboard.domain

import com.sekota.pmoebdesk.features.dashboard.domain.DashboardMetrics
import com.sekota.pmoebdesk.features.dashboard.domain.OpenProjectRepository

class GetBodDashboardDataUseCase(
    private val repository: OpenProjectRepository
) {
    suspend operator fun invoke(baseUrl: String, apiKey: String): DashboardMetrics {
        return repository.getDashboardMetrics(baseUrl, apiKey)
    }
}
