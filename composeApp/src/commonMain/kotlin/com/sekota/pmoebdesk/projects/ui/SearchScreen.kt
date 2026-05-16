package com.sekota.pmoebdesk.projects.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sekota.pmoebdesk.core.ui.StatusAmber
import com.sekota.pmoebdesk.core.ui.StatusAmberBackground
import com.sekota.pmoebdesk.core.ui.StatusGreen
import com.sekota.pmoebdesk.core.ui.StatusGreenBackground
import com.sekota.pmoebdesk.core.ui.StatusRed
import com.sekota.pmoebdesk.core.ui.StatusRedBackground
import com.sekota.pmoebdesk.projects.ui.components.FilterBar
import com.sekota.pmoebdesk.projects.ui.components.ProjectCard
import com.sekota.pmoebdesk.projects.ui.components.SearchBarFull

@Composable
fun ProjectSearchScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SearchBarFull()
        }
        item {
            FilterBar()
        }
        item {
            ProjectCard(
                name = "Project Orion",
                status = "On Track",
                statusColor = StatusGreen,
                statusBg = StatusGreenBackground,
                budget = "$1.2M",
                deadline = "Dec 2024",
                startedDate = "Jan 15, 2024",
                teamCount = 5
            )
        }
        item {
            ProjectCard(
                name = "Nexus Integration",
                status = "At Risk",
                statusColor = StatusAmber,
                statusBg = StatusAmberBackground,
                budget = "$850k",
                deadline = "Oct 2024",
                startedDate = "Mar 02, 2024",
                teamCount = 3
            )
        }
        item {
            ProjectCard(
                name = "Security Audit",
                status = "Critical",
                statusColor = StatusRed,
                statusBg = StatusRedBackground,
                budget = "$450k",
                deadline = "Aug 2024",
                startedDate = "Feb 12, 2024",
                isWarning = true
            )
        }
        item {
            ProjectCard(
                name = "Data Migration",
                status = "On Track",
                statusColor = StatusGreen,
                statusBg = StatusGreenBackground,
                budget = "$2.1M",
                deadline = "Mar 2025",
                startedDate = "May 20, 2024",
                teamCount = 1
            )
        }
    }
}
