package com.sekota.pmoebdesk.domain.model

enum class RAGStatus {
    GREEN, AMBER, RED
}

data class DashboardMetrics(
    val strategicRagStatus: RAGStatus,
    val netProgressPercentage: Double,
    val strategicGrowthHours: Double,
    val businessAsUsualHours: Double,
    val milestones: List<Milestone>,
    val risks: List<Risk>,
    val exceptions: List<ProjectException>,
    val boardInterventions: List<BoardIntervention>
)

data class Milestone(val title: String, val month: String, val isCompleted: Boolean = false)
data class Risk(val category: String, val likelihood: Int, val impact: Int)
data class ProjectException(val projectName: String, val mitigationSummary: String)
data class BoardIntervention(val description: String, val isApproved: Boolean = false)
