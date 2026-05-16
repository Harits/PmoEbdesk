package com.sekota.pmoebdesk.projects.domain.repository

import com.sekota.pmoebdesk.projects.domain.model.Project

interface ProjectRepository {
    suspend fun searchProjects(
        query: String? = null,
        parentId: Int? = null,
        allowedIds: List<Int>? = null
    ): List<Project>
}
