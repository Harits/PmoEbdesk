package com.sekota.pmoebdesk

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
    var metrics by remember { mutableStateOf<DashboardMetrics?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            println("AppContainer: Starting data fetch...")
            val result = repository.getDashboardMetrics(config.OPENPROJECT_URL, config.OPENPROJECT_API_KEY)
            metrics = result
            println("AppContainer: Data fetch completed. Metrics received.")
        } catch (e: Throwable) {
            println("AppContainer: Data fetch failed: ${e.message}")
            error = e.message ?: "Unknown error"
        }
    }

    if (metrics != null) {
        App(metrics)
    } else if (error != null) {
        Text("Error: \$error")
    } else {
        Text("Loading dashboard data...")
    }
}
