package com.sekota.pmoebdesk.features.projects.domain

class SearchProjectsUseCase(private val repository: ProjectSearchRepository) {
    suspend operator fun invoke(baseUrl: String, apiKey: String, query: String, statusFilter: String?): List<ProjectItem> {
        return repository.searchProjects(baseUrl, apiKey, query, statusFilter)
    }
}
