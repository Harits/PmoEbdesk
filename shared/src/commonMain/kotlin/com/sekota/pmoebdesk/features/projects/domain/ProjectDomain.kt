package com.sekota.pmoebdesk.features.projects.domain

import com.sekota.pmoebdesk.features.dashboard.domain.RAGStatus

data class ProjectItem(
    val name: String,
    val status: String,
    val statusColorType: RAGStatus,
    val budget: String,
    val deadline: String,
    val startedDate: String,
    val teamCount: Int,
    val isWarning: Boolean = false
)

interface ProjectSearchRepository {
    suspend fun searchProjects(baseUrl: String, apiKey: String, query: String, statusFilter: String?): List<ProjectItem>
}
