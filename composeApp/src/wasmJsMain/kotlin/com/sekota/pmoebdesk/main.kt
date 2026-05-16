package com.sekota.pmoebdesk

import androidx.compose.runtime.LaunchedEffect
import com.sekota.pmoebdesk.dashboard.domain.model.DashboardMetrics
import com.sekota.pmoebdesk.dashboard.domain.repository.OpenProjectRepository
import com.sekota.pmoebdesk.dashboard.data.repository.MockOpenProjectRepositoryImpl
import com.sekota.pmoebdesk.dashboard.data.repository.ProductionOpenProjectRepositoryImpl
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.repository.ProjectRepository
import com.sekota.pmoebdesk.projects.data.repository.MockProjectRepositoryImpl
import com.sekota.pmoebdesk.projects.data.repository.ProductionProjectRepositoryImpl
import io.ktor.client.*
import androidx.compose.runtime.rememberCoroutineScope
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                    error = "Failed to load config.json."
                }
            } catch (e: Exception) {
                error = "Exception: ${e.message}"
            }
        }

        if (config != null) {
            val client = HttpClient(Js) {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
            
            val dashboardRepo = if (config!!.USE_MOCK_DATA) {
                MockOpenProjectRepositoryImpl()
            } else {
                ProductionOpenProjectRepositoryImpl(client)
            }

            val projectRepo = if (config!!.USE_MOCK_DATA) {
                MockProjectRepositoryImpl()
            } else {
                ProductionProjectRepositoryImpl(client, config!!.OPENPROJECT_URL, config!!.OPENPROJECT_API_KEY)
            }

            AppContainer(config!!, dashboardRepo, projectRepo)
        } else if (error != null) {
            Text("Error: $error")
        } else {
            Text("Loading config...")
        }
    }
}

@Composable
fun AppContainer(
    config: AppConfig, 
    dashboardRepo: OpenProjectRepository,
    projectRepo: ProjectRepository
) {
    var metrics by remember { mutableStateOf<DashboardMetrics?>(null) }
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var searchJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        try {
            metrics = dashboardRepo.getDashboardMetrics(config.OPENPROJECT_URL, config.OPENPROJECT_API_KEY)
            // Initial project load
            isSearching = true
            projects = projectRepo.searchProjects(
                parentId = config.PROJECT_PARENT_ID,
                allowedIds = config.ALLOWED_PROJECT_IDS?.split(",")?.mapNotNull { it.trim().toIntOrNull() }
            )
            isSearching = false
        } catch (e: Throwable) {
            error = e.message ?: "Unknown error"
        }
    }

    fun handleSearch(query: String) {
        searchQuery = query
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(300)
            isSearching = true
            projects = projectRepo.searchProjects(
                query = query,
                parentId = config.PROJECT_PARENT_ID,
                allowedIds = config.ALLOWED_PROJECT_IDS?.split(",")?.mapNotNull { it.trim().toIntOrNull() }
            )
            isSearching = false
        }
    }

    if (error != null) {
        Text("Error: $error")
    } else {
        App(
            metrics = metrics,
            projects = projects,
            searchQuery = searchQuery,
            onSearchQueryChange = ::handleSearch,
            isSearching = isSearching
        )
    }
}
