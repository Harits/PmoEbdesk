package com.sekota.pmoebdesk.projects.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sekota.pmoebdesk.core.ui.*
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus

@Composable
fun FilterBar(
    selectedStatus: ProjectStatus?,
    onStatusSelected: (ProjectStatus?) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = selectedStatus == null,
            onClick = { onStatusSelected(null) },
            label = { Text("All") }
        )
        
        ProjectStatus.entries.filter { it != ProjectStatus.UNKNOWN }.forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                label = { Text(status.name.replace("_", " ").lowercase().capitalize()) },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(getStatusColor(status))
                    )
                }
            )
        }
    }
}

private fun getStatusColor(status: ProjectStatus): Color {
    return when (status) {
        ProjectStatus.ON_TRACK -> StatusGreen
        ProjectStatus.AT_RISK -> StatusAmber
        ProjectStatus.OFF_TRACK -> StatusRed
        ProjectStatus.NOT_STARTED -> StatusBlue
        ProjectStatus.FINISHED -> StatusPurple
        ProjectStatus.DISCONTINUED -> StatusBrown
        ProjectStatus.ON_HOLD -> Color.DarkGray
        ProjectStatus.NOT_SET -> Color.Gray
        ProjectStatus.UNKNOWN -> Color.Gray
    }
}

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
