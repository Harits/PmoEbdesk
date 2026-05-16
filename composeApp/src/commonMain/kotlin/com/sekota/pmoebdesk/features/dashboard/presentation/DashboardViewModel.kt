package com.sekota.pmoebdesk.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sekota.pmoebdesk.features.dashboard.domain.DashboardMetrics
import com.sekota.pmoebdesk.features.dashboard.domain.GetBodDashboardDataUseCase
import com.sekota.pmoebdesk.features.dashboard.domain.OpenProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val metrics: DashboardMetrics) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel(
    private val useCase: GetBodDashboardDataUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun loadDashboard(baseUrl: String, apiKey: String) {
        viewModelScope.launch {
            _state.value = DashboardState.Loading
            try {
                val metrics = useCase(baseUrl, apiKey)
                _state.value = DashboardState.Success(metrics)
            } catch (e: Exception) {
                _state.value = DashboardState.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }
}
