package com.sekota.pmoebdesk.data.repository

import com.sekota.pmoebdesk.domain.model.*
import com.sekota.pmoebdesk.domain.repository.DashboardRepository

class DashboardRepositoryImpl : DashboardRepository {
    override suspend fun getDashboardMetrics(): DashboardMetrics {
        // Mock data for now
        return DashboardMetrics(
            strategicRagStatus = RAGStatus.GREEN,
            netProgressPercentage = 68.0,
            strategicGrowthHours = 1250.0,
            businessAsUsualHours = 450.0,
            milestones = listOf(
                Milestone("Product Launch", "OCT", isCompleted = true),
                Milestone("Market Entry", "NOV", isCompleted = false),
                Milestone("Q3 Audit", "DEC", isCompleted = false)
            ),
            risks = listOf(
                Risk("Resource Attrition", 4, 5),
                Risk("Dependency Delay", 3, 4)
            ),
            exceptions = listOf(
                ProjectException("Project Orion", "Hiring 2 senior architects to resolve technical bottleneck."),
                ProjectException("Nexus Integration", "Contractor dispute; legal mediation scheduled for Friday."),
                ProjectException("Data Migration", "Storage limits reached; approving emergency cloud burst.")
            ),
            boardInterventions = listOf(
                BoardIntervention("Approve shift of 3 devs from Project B to Project A to secure Q3 goal."),
                BoardIntervention("Authorize $50k contingency fund release for Orion licensing fees.")
            )
        )
    }
}
