package com.sekota.pmoebdesk.dashboard.domain.usecase

import com.sekota.pmoebdesk.dashboard.domain.model.DashboardMetrics
import com.sekota.pmoebdesk.dashboard.domain.repository.OpenProjectRepository

class GetBodDashboardDataUseCase(
    private val repository: OpenProjectRepository
) {
    suspend operator fun invoke(
        baseUrl: String, 
        apiKey: String,
        projectId: Int? = null,
        parentProjectId: Int? = null,
        allowedProjectIds: List<Int>? = null
    ): DashboardMetrics {
        return repository.getDashboardMetrics(baseUrl, apiKey, projectId, parentProjectId, allowedProjectIds)
    }
}
