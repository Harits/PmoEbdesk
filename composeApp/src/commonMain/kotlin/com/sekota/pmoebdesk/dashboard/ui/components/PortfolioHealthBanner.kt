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
import com.sekota.pmoebdesk.core.ui.StatusGreen
import com.sekota.pmoebdesk.core.ui.StatusGreenBackground

@Composable
fun PortfolioHealthBanner() {
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
                modifier = Modifier.size(56.dp).background(StatusGreenBackground, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(24.dp)) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width * 0.2f, size.height * 0.5f)
                        lineTo(size.width * 0.45f, size.height * 0.75f)
                        lineTo(size.width * 0.8f, size.height * 0.25f)
                    }
                    drawPath(path, color = StatusGreen, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text("Portfolio Health", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text("Strategic RAG Status: Green", style = MaterialTheme.typography.headlineLarge, color = Color.Black)
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = StatusGreenBackground,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        drawLine(color = StatusGreen, start = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.8f), end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.2f), strokeWidth = 2.dp.toPx())
                        drawLine(color = StatusGreen, start = androidx.compose.ui.geometry.Offset(size.width * 0.4f, size.height * 0.2f), end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.2f), strokeWidth = 2.dp.toPx())
                        drawLine(color = StatusGreen, start = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.2f), end = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.6f), strokeWidth = 2.dp.toPx())
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("+4.2% Monthly Uplift", color = StatusGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
