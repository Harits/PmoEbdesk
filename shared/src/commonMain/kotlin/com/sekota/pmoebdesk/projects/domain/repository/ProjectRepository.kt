package com.sekota.pmoebdesk.projects.domain.repository

import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus

interface ProjectRepository {
    suspend fun searchProjects(
        query: String? = null,
        parentId: Int? = null,
        allowedIds: List<Int>? = null,
        status: ProjectStatus? = null
    ): List<Project>
}
