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

        LaunchedEffect(Unit) {
            config = loadConfig()
        }

        if (config != null) {
            val repository = if (config!!.USE_MOCK_DATA) {
                MockOpenProjectRepositoryImpl()
            } else {
                ProductionOpenProjectRepositoryImpl()
            }

            AppContainer(config!!, repository)
        } else {
            Text("Loading config...")
        }
    }
}

@Composable
fun AppContainer(config: AppConfig, repository: OpenProjectRepository) {
    var metrics by remember { mutableStateOf<DashboardMetrics?>(null) }

    LaunchedEffect(Unit) {
        metrics = repository.getDashboardMetrics(config.OPENPROJECT_URL, config.OPENPROJECT_API_KEY)
    }

    if (metrics != null) {
        App(metrics)
    } else {
        Text("Loading dashboard data...")
    }
}
