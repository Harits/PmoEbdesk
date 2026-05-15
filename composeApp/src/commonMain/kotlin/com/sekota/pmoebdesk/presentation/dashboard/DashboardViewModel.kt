package com.sekota.pmoebdesk.presentation.dashboard

import com.sekota.pmoebdesk.domain.model.DashboardMetrics
import com.sekota.pmoebdesk.domain.usecase.GetDashboardMetricsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardViewModel(private val getDashboardMetricsUseCase: GetDashboardMetricsUseCase) {
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        loadMetrics()
    }

    private fun loadMetrics() {
        viewModelScope.launch {
            try {
                val metrics = getDashboardMetricsUseCase()
                _uiState.value = DashboardState.Success(metrics)
            } catch (e: Exception) {
                _uiState.value = DashboardState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val metrics: DashboardMetrics) : DashboardState()
    data class Error(val message: String) : DashboardState()
}
