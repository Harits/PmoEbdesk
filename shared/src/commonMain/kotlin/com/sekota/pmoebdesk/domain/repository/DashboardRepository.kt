package com.sekota.pmoebdesk.domain.repository

import com.sekota.pmoebdesk.domain.model.DashboardMetrics

interface DashboardRepository {
    suspend fun getDashboardMetrics(): DashboardMetrics
}
