package com.sekota.pmoebdesk.projects.domain.model

enum class ProjectStatus {
    ON_TRACK, AT_RISK, CRITICAL, COMPLETED, ON_HOLD, UNKNOWN
}

data class Project(
    val id: Int,
    val identifier: String,
    val name: String,
    val status: ProjectStatus,
    val budget: String? = null,
    val deadline: String? = null,
    val startedDate: String? = null,
    val teamCount: Int = 0,
    val isWarning: Boolean = false
)
