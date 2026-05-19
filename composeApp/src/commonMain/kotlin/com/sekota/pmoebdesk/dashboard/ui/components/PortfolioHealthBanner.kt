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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.core.ui.*
import com.sekota.pmoebdesk.dashboard.domain.model.RAGStatus

@Composable
fun PortfolioHealthBanner(status: RAGStatus, trend: Double) {
    val statusColor = when(status) {
        RAGStatus.GREEN -> StatusGreen
        RAGStatus.AMBER -> StatusAmber
        RAGStatus.RED -> StatusRed
    }
    val statusBackground = when(status) {
        RAGStatus.GREEN -> StatusGreenBackground
        RAGStatus.AMBER -> StatusAmberBackground
        RAGStatus.RED -> StatusRedBackground
    }
    val statusText = when(status) {
        RAGStatus.GREEN -> "Green"
        RAGStatus.AMBER -> "Amber"
        RAGStatus.RED -> "Red"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).background(statusBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(24.dp)) {
                    if (status == RAGStatus.GREEN) {
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width * 0.2f, size.height * 0.5f)
                            lineTo(size.width * 0.45f, size.height * 0.75f)
                            lineTo(size.width * 0.8f, size.height * 0.25f)
                        }
                        drawPath(path, color = statusColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                    } else {
                        drawCircle(color = statusColor, radius = size.width / 4f)
                    }
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) { Text("Portfolio Health", style = MaterialTheme.typography.bodyMedium, color = Color.Gray); Spacer(modifier = Modifier.width(8.dp)); InfoTooltip("Displays the overall health of the project portfolio using a RAG (Red, Amber, Green) status. It represents a consolidated view of budget, schedule, and quality across all active projects.") }
                Text("Strategic RAG Status: $statusText", style = MaterialTheme.typography.headlineLarge, color = Color.Black)
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = statusBackground,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        drawLine(color = statusColor, start = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.8f), end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.2f), strokeWidth = 2.dp.toPx())
                        drawLine(color = statusColor, start = androidx.compose.ui.geometry.Offset(size.width * 0.4f, size.height * 0.2f), end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.2f), strokeWidth = 2.dp.toPx())
                        drawLine(color = statusColor, start = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.2f), end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.6f), strokeWidth = 2.dp.toPx())
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${if(trend >= 0) "+" else ""}$trend% Monthly Uplift", color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
