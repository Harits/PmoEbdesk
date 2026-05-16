package com.sekota.pmoebdesk.projects.domain.usecase

import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus
import com.sekota.pmoebdesk.projects.domain.repository.ProjectRepository

class SearchProjectsUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(
        query: String? = null,
        parentId: Int? = null,
        allowedIds: List<Int>? = null,
        status: ProjectStatus? = null
    ): List<Project> {
        return repository.searchProjects(query, parentId, allowedIds, status)
    }
}
