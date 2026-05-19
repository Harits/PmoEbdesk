package com.sekota.pmoebdesk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sekota.pmoebdesk.core.ui.*
import com.sekota.pmoebdesk.dashboard.domain.model.*
import com.sekota.pmoebdesk.dashboard.ui.DashboardContent
import com.sekota.pmoebdesk.projects.ui.ProjectSearchScreen
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Devices.DESKTOP

import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus
import kotlinx.coroutines.delay

@Composable
fun App(
    metrics: DashboardMetrics?,
    projects: List<Project>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatus: ProjectStatus?,
    onStatusChange: (ProjectStatus?) -> Unit,
    onProjectSelected: (Project) -> Unit,
    selectedProject: Project? = null,
    onResetSelection: () -> Unit = {},
    isSearching: Boolean = false,
) {
    DashboardTheme {
        var currentScreen by remember { mutableStateOf("dashboard") }
        
        Column(modifier = Modifier.fillMaxSize().background(MainBackground)) {
            TopBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearchFocus = { currentScreen = "search" },
                onTitleClick = { 
                    onResetSelection()
                    currentScreen = "dashboard" 
                },
                selectedProjectName = selectedProject?.name ?: "All Projects"
            )
            Box(modifier = Modifier.weight(1f)) {
                if (currentScreen == "dashboard") {
                    if (metrics != null) {
                        DashboardContent(metrics, modifier = Modifier.fillMaxSize())
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    ProjectSearchScreen(
                        projects = projects,
                        query = searchQuery,
                        selectedStatus = selectedStatus,
                        onStatusChange = onStatusChange,
                        onProjectSelected = { project ->
                            onProjectSelected(project)
                            currentScreen = "dashboard"
                        },
                        isLoading = isSearching,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Footer()
        }
    }
}

@Preview(device = DESKTOP)
@Composable
fun DashboardPreview() {
    val sampleMetrics = DashboardMetrics(
        strategicRagStatus = RAGStatus.GREEN,
        netProgressPercentage = 68.0,
        strategicGrowthHours = 1250.0,
        businessAsUsualHours = 450.0,
        milestones = listOf(
            Milestone("Product Launch", "OCT"),
            Milestone("Market Entry", "NOV"),
            Milestone("Q3 Audit", "DEC")
        ),
        risks = listOf(
            Risk("Resource Attrition", 4, 5, RiskLevel.MEDIUM),
            Risk("Dependency Delay", 3, 4, RiskLevel.LOW)
        ),
        exceptions = listOf(
            ProjectException("Project Orion", "Hiring 2 senior architects to resolve technical bottleneck."),
            ProjectException("Nexus Integration", "Contractor dispute; legal mediation scheduled for Friday."),
            ProjectException("Data Migration", "Storage limits reached; approving emergency cloud burst.")
        ),
        boardInterventions = listOf(
            BoardIntervention("Approve shift of 3 devs from Project B to Project A to secure Q3 goal."),
            BoardIntervention("Authorize $50k contingency fund release for Orion licensing fees.")
        )
    )
    App(
        metrics = sampleMetrics,
        projects = emptyList(),
        searchQuery = "",
        onSearchQueryChange = {},
        selectedStatus = null,
        onStatusChange = {},
        onProjectSelected = {}
    )
}

@Preview(device = DESKTOP)
@Composable
fun SearchPreview() {
    DashboardTheme {
        ProjectSearchScreen(
            projects = listOf(
                Project(1, "p1", "Project Orion", ProjectStatus.ON_TRACK, "Rp 1.2B", "Dec 2024", "Jan 15, 2024", 5)
            ),
            query = "",
            selectedStatus = null,
            onStatusChange = {},
            onProjectSelected = {},
            modifier = Modifier.fillMaxSize().background(MainBackground)
        )
    }
}
