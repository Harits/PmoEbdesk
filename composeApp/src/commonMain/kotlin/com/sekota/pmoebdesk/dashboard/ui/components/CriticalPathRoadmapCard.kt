package com.sekota.pmoebdesk.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sekota.pmoebdesk.core.ui.*
import androidx.compose.ui.unit.sp

import com.sekota.pmoebdesk.dashboard.domain.model.Milestone

@Composable
fun CriticalPathRoadmapCard(milestones: List<Milestone>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Critical Path Roadmap", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy); Spacer(modifier = Modifier.width(8.dp)); InfoTooltip("Visualizes the most important sequence of milestones. Any delay in these items will directly cause a delay in the final delivery of the entire portfolio.") }
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                HorizontalDivider(modifier = Modifier.align(Alignment.Center).padding(horizontal = 40.dp), thickness = 1.dp, color = Color.LightGray)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    if (milestones.isEmpty()) {
                        Text("No upcoming milestones", modifier = Modifier.align(Alignment.CenterVertically), color = Color.Gray)
                    } else {
                        milestones.take(4).forEach { milestone ->
                            TimelinePoint(milestone.date.uppercase(), milestone.title, isCompleted = false)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                color = Color(0xFFF1F4F8),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        drawCircle(color = PrimaryNavy, style = Stroke(width = 1.5.dp.toPx()))
                        drawCircle(color = PrimaryNavy, radius = 1.dp.toPx(), center = center.copy(y = center.y - 4.dp.toPx()))
                        drawLine(color = PrimaryNavy, start = center.copy(y = center.y - 1.dp.toPx()), end = center.copy(y = center.y + 5.dp.toPx()), strokeWidth = 2.dp.toPx())
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    val statusText = if (milestones.isNotEmpty()) {
                        "Next milestone '${milestones.first().title}' is currently tracking on schedule."
                    } else {
                        "No critical path delays reported."
                    }
                    Text(
                        statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun TimelinePoint(month: String, title: String, isCompleted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(if (isCompleted) PrimaryNavy else Color.LightGray, CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(month, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
