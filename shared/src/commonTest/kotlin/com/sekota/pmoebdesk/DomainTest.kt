package com.sekota.pmoebdesk
import com.sekota.pmoebdesk.features.dashboard.domain.*

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DomainTest {
    @Test
    fun testRiskCalculation() {
        val risk = Risk("High Impact Issue", 4, 5)
        assertEquals(4, risk.probability)
        assertEquals(5, risk.impact)
    }

    @Test
    fun testDashboardMetricsInit() {
        val metrics = DashboardMetrics(
            strategicRagStatus = RAGStatus.GREEN,
            netProgressPercentage = 95.0,
            strategicGrowthHours = 100.0,
            businessAsUsualHours = 50.0,
            milestones = emptyList(),
            risks = emptyList(),
            exceptions = emptyList(),
            boardInterventions = emptyList()
        )

        assertEquals(RAGStatus.GREEN, metrics.strategicRagStatus)
        assertEquals(95.0, metrics.netProgressPercentage)
    }
}
