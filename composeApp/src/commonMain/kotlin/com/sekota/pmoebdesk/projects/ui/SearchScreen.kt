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

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import com.sekota.pmoebdesk.core.ui.*
import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus
import com.sekota.pmoebdesk.projects.ui.components.FilterBar
import com.sekota.pmoebdesk.projects.ui.components.ProjectCard
import com.sekota.pmoebdesk.projects.ui.components.SearchBarFull

@Composable
fun ProjectSearchScreen(
    projects: List<Project>,
    query: String,
    onQueryChange: (String) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SearchBarFull(query, onQueryChange)
        }
        item {
            FilterBar()
        }
        
        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (projects.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No projects found matching \"$query\"", color = Color.Gray)
                }
            }
        } else {
            items(projects.size) { index ->
                val project = projects[index]
                val (color, bg) = when (project.status) {
                    ProjectStatus.ON_TRACK -> StatusGreen to StatusGreenBackground
                    ProjectStatus.AT_RISK -> StatusAmber to StatusAmberBackground
                    ProjectStatus.CRITICAL -> StatusRed to StatusRedBackground
                    ProjectStatus.COMPLETED -> Color.Gray to Color(0xFFEEEEEE)
                    ProjectStatus.ON_HOLD -> Color.DarkGray to Color(0xFFDDDDDD)
                    ProjectStatus.UNKNOWN -> Color.Gray to Color(0xFFF5F5F5)
                }
                
                ProjectCard(
                    name = project.name,
                    status = project.status.name.replace("_", " ").lowercase().capitalize(),
                    statusColor = color,
                    statusBg = bg,
                    budget = project.budget ?: "N/A",
                    deadline = project.deadline ?: "N/A",
                    startedDate = project.startedDate ?: "N/A",
                    teamCount = project.teamCount,
                    isWarning = project.isWarning
                )
            }
        }
    }
}

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
