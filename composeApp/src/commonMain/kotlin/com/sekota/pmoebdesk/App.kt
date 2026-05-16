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

@Composable
fun App(metrics: DashboardMetrics?) {
    DashboardTheme {
        var currentScreen by remember { mutableStateOf("dashboard") }
        
        if (metrics != null) {
            Column(modifier = Modifier.fillMaxSize().background(MainBackground)) {
                TopBar(
                    onSearchFocus = { currentScreen = "search" },
                    onTitleClick = { currentScreen = "dashboard" }
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (currentScreen == "dashboard") {
                        DashboardContent(metrics, modifier = Modifier.fillMaxSize())
                    } else {
                        ProjectSearchScreen(modifier = Modifier.fillMaxSize())
                    }
                }
                Footer()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
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
            Risk("Resource Attrition", 4, 5),
            Risk("Dependency Delay", 3, 4)
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
    App(metrics = sampleMetrics)
}

@Preview(device = DESKTOP)
@Composable
fun SearchPreview() {
    DashboardTheme {
        ProjectSearchScreen(modifier = Modifier.fillMaxSize().background(MainBackground))
    }
}
