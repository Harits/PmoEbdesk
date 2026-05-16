package com.sekota.pmoebdesk.projects.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    selectedStatus: ProjectStatus?,
    onStatusChange: (ProjectStatus?) -> Unit,
    onProjectSelected: (Project) -> Unit,
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
            FilterBar(selectedStatus, onStatusChange)
        }
        
        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (projects.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
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
                    deadline = project.deadline ?: "N/A",
                    startedDate = project.startedDate ?: "N/A",
                    teamCount = project.teamCount,
                    isWarning = project.isWarning,
                    onClick = { onProjectSelected(project) }
                )
            }
        }
    }
}

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
