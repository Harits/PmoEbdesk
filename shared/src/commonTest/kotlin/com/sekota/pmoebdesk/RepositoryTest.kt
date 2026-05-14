package com.sekota.pmoebdesk

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepositoryTest {
    @Test
    fun testGetMockDashboardMetrics() = runTest {
        val repo = MockOpenProjectRepositoryImpl()
        val metrics = repo.getDashboardMetrics("http://localhost", "apikey")

        assertEquals(RAGStatus.AMBER, metrics.strategicRagStatus)
        assertEquals(68.0, metrics.netProgressPercentage)
        assertTrue(metrics.milestones.isNotEmpty())
    }
}
