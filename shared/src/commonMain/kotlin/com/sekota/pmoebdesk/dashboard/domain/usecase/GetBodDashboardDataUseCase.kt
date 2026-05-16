package com.sekota.pmoebdesk.dashboard.domain.usecase

import com.sekota.pmoebdesk.dashboard.domain.model.DashboardMetrics
import com.sekota.pmoebdesk.dashboard.domain.repository.OpenProjectRepository

class GetBodDashboardDataUseCase(
    private val repository: OpenProjectRepository
) {
    suspend operator fun invoke(baseUrl: String, apiKey: String): DashboardMetrics {
        return repository.getDashboardMetrics(baseUrl, apiKey)
    }
}
