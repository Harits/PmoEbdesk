package com.sekota.pmoebdesk.features.projects.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekota.pmoebdesk.features.projects.domain.ProjectItem
import com.sekota.pmoebdesk.features.projects.domain.SearchProjectsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

sealed class ProjectSearchState {
    object Loading : ProjectSearchState()
    data class Success(val projects: List<ProjectItem>) : ProjectSearchState()
    data class Error(val message: String) : ProjectSearchState()
}

class ProjectSearchViewModel(
    private val searchProjectsUseCase: SearchProjectsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ProjectSearchState>(ProjectSearchState.Loading)
    val state: StateFlow<ProjectSearchState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private var searchJob: Job? = null

    private var baseUrl: String = ""
    private var apiKey: String = ""

    fun initialize(url: String, key: String) {
        baseUrl = url
        apiKey = key
        executeSearch()
    }

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            executeSearch()
        }
    }

    fun onFilterChanged(filter: String) {
        _statusFilter.value = filter
        executeSearch()
    }

    private fun executeSearch() {
        if (baseUrl.isEmpty() || apiKey.isEmpty()) return

        viewModelScope.launch {
            _state.value = ProjectSearchState.Loading
            try {
                val projects = searchProjectsUseCase(baseUrl, apiKey, _searchQuery.value, _statusFilter.value)
                _state.value = ProjectSearchState.Success(projects)
            } catch (e: Exception) {
                _state.value = ProjectSearchState.Error(e.message ?: "Failed to search projects")
            }
        }
    }
}
