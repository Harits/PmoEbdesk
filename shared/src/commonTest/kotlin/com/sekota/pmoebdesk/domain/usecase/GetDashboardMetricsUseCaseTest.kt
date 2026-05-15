package com.sekota.pmoebdesk.domain.usecase

import com.sekota.pmoebdesk.domain.model.*
import com.sekota.pmoebdesk.domain.repository.DashboardRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDashboardMetricsUseCaseTest {

    private val fakeRepository = object : DashboardRepository {
        override suspend fun getDashboardMetrics(): DashboardMetrics {
            return DashboardMetrics(
                strategicRagStatus = RAGStatus.GREEN,
                netProgressPercentage = 68.0,
                strategicGrowthHours = 1250.0,
                businessAsUsualHours = 450.0,
                milestones = listOf(Milestone("Test Milestone", "JAN")),
                risks = listOf(Risk("Test Risk", 3, 4)),
                exceptions = listOf(ProjectException("Test Project", "Test Mitigation")),
                boardInterventions = listOf(BoardIntervention("Test Intervention"))
            )
        }
    }

    private val useCase = GetDashboardMetricsUseCase(fakeRepository)

    @Test
    fun testInvokeReturnsMetricsFromRepository() = runTest {
        val result = useCase()
        assertEquals(RAGStatus.GREEN, result.strategicRagStatus)
        assertEquals(68.0, result.netProgressPercentage)
        assertEquals("Test Milestone", result.milestones.first().title)
    }
}
