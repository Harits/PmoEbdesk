package com.sekota.pmoebdesk.dashboard.domain.model

enum class RAGStatus {
    RED, AMBER, GREEN
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

data class Milestone(
    val title: String,
    val date: String
)

data class Risk(
    val title: String,
    val probability: Int, // 1 to 5
    val impact: Int       // 1 to 5
)

data class ProjectException(
    val projectName: String,
    val mitigationSummary: String
)

data class BoardIntervention(
    val description: String
)
