package com.sekota.pmoebdesk.dashboard.domain.model

enum class RAGStatus {
    RED, AMBER, GREEN
}

data class DashboardMetrics(
    val strategicRagStatus: RAGStatus,
    val netProgressPercentage: Double,
    val trendPercentage: Double = 0.0,
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

enum class RiskLevel {
    LOW, MEDIUM, HIGH
}

data class Risk(
    val title: String,
    val probability: Int, // 1 to 5
    val impact: Int,       // 1 to 5
    val level: RiskLevel = RiskLevel.LOW
)

data class ProjectException(
    val projectName: String,
    val mitigationSummary: String
)

data class BoardIntervention(
    val description: String
)
