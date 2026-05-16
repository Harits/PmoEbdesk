package com.sekota.pmoebdesk

import com.sekota.pmoebdesk.di.initKoin
import com.sekota.pmoebdesk.di.viewModelModule

import com.sekota.pmoebdesk.features.dashboard.domain.*
import com.sekota.pmoebdesk.features.dashboard.data.*

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        var config by remember { mutableStateOf<AppConfig?>(null) }
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            try {
                val loadedConfig = loadConfig()
                if (loadedConfig != null) {
                    config = loadedConfig
                } else {
                    error = "Failed to load config.json. Please check if the file exists in resources/ and has the correct format. Check browser console for details."
                }
            } catch (e: Exception) {
                error = "Exception: ${e.message}\n${e.stackTraceToString()}"
            }
        }

        if (config != null) {
            val repository = if (config!!.USE_MOCK_DATA) {
                MockOpenProjectRepositoryImpl()
            } else {
                ProductionOpenProjectRepositoryImpl()
            }

            AppContainer(config!!, repository)
        } else if (error != null) {
            Text("Error: $error")
        } else {
            Text("Loading config...")
        }
    }
}

@Composable
fun AppContainer(config: AppConfig, repository: OpenProjectRepository) {
    val projectRepository = remember {
        if (config.USE_MOCK_DATA) {
            com.sekota.pmoebdesk.features.projects.data.MockProjectSearchRepositoryImpl()
        } else {
            com.sekota.pmoebdesk.features.projects.data.ProductionProjectSearchRepositoryImpl()
        }
    }
    val viewModel = remember { com.sekota.pmoebdesk.features.dashboard.presentation.DashboardViewModel(GetBodDashboardDataUseCase(repository)) }
    val projectViewModel = remember { com.sekota.pmoebdesk.features.projects.presentation.ProjectSearchViewModel(com.sekota.pmoebdesk.features.projects.domain.SearchProjectsUseCase(projectRepository)) }
    App(viewModel, projectViewModel, config.OPENPROJECT_URL, config.OPENPROJECT_API_KEY)
}
