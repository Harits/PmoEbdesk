package com.sekota.pmoebdesk

class GetBodDashboardDataUseCase(
    private val repository: OpenProjectRepository
) {
    suspend operator fun invoke(baseUrl: String, apiKey: String): DashboardMetrics {
        return repository.getDashboardMetrics(baseUrl, apiKey)
    }
}
