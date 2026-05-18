package com.sekota.pmoebdesk.projects.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.core.ui.*
import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus
import com.sekota.pmoebdesk.projects.ui.components.FilterBar
import com.sekota.pmoebdesk.projects.ui.components.ProjectCard

@Composable
fun ProjectSearchScreen(
    projects: List<Project>,
    query: String,
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No projects found",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We couldn't find anything matching \"$query\".\nTry adjusting your filters or search terms.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        } else {
            items(projects.size) { index ->
                val project = projects[index]
                val (color, bg) = when (project.status) {
                    ProjectStatus.ON_TRACK -> StatusGreen to StatusGreenBackground
                    ProjectStatus.AT_RISK -> StatusAmber to StatusAmberBackground
                    ProjectStatus.OFF_TRACK -> StatusRed to StatusRedBackground
                    ProjectStatus.NOT_STARTED -> StatusBlue to StatusBlueBackground
                    ProjectStatus.FINISHED -> StatusPurple to StatusPurpleBackground
                    ProjectStatus.DISCONTINUED -> StatusBrown to StatusBrownBackground
                    ProjectStatus.ON_HOLD -> Color.DarkGray to Color(0xFFDDDDDD)
                    ProjectStatus.NOT_SET -> Color.Gray to Color(0xFFF5F5F5)
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
