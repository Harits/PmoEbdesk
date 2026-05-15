package com.sekota.pmoebdesk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sekota.pmoebdesk.data.repository.DashboardRepositoryImpl
import com.sekota.pmoebdesk.domain.usecase.GetDashboardMetricsUseCase
import com.sekota.pmoebdesk.presentation.dashboard.*
import com.sekota.pmoebdesk.presentation.projects.ProjectsScreen
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Devices.DESKTOP

enum class NavDestination {
    DASHBOARD,
    PROJECTS
}

@Composable
fun App() {
    val repository = remember { DashboardRepositoryImpl() }
    val getDashboardMetricsUseCase = remember { GetDashboardMetricsUseCase(repository) }
    val viewModel = remember { DashboardViewModel(getDashboardMetricsUseCase) }

    val state by viewModel.uiState.collectAsState()

    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }

    DashboardTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            Sidebar(
                modifier = Modifier.width(260.dp),
                currentDestination = currentDestination,
                onNavigate = { currentDestination = it }
            )
            Column(modifier = Modifier.weight(1f)) {
                TopBar()
                when (currentDestination) {
                    NavDestination.DASHBOARD -> {
                        when (state) {
                            is DashboardState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is DashboardState.Success -> {
                                val metrics = (state as DashboardState.Success).metrics
                                DashboardContent(metrics, modifier = Modifier.fillMaxSize())
                            }
                            is DashboardState.Error -> {
                                Text("Error loading dashboard data")
                            }
                        }
                    }
                    NavDestination.PROJECTS -> {
                        ProjectsScreen(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

@Preview(device = DESKTOP)
@Composable
fun AppDesktopPreview() {
    App()
}
