package com.sekota.pmoebdesk.domain.usecase

import com.sekota.pmoebdesk.domain.model.DashboardMetrics
import com.sekota.pmoebdesk.domain.repository.DashboardRepository

class GetDashboardMetricsUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): DashboardMetrics {
        return repository.getDashboardMetrics()
    }
}
