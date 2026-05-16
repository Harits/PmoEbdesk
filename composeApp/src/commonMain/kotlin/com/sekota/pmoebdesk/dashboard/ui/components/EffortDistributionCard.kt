package com.sekota.pmoebdesk.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.core.ui.PrimaryNavy

@Composable
fun EffortDistributionCard(
    strategicHours: Double,
    bauHours: Double,
    modifier: Modifier = Modifier
) {
    val total = strategicHours + bauHours
    val strategicPercentage = if (total > 0) (strategicHours / total) * 100 else 60.0
    val bauPercentage = if (total > 0) (bauHours / total) * 100 else 40.0
    val sweepAngle = (strategicPercentage / 100 * 360).toFloat()

    Card(
        modifier = modifier.height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Effort Distribution", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
                Canvas(modifier = Modifier.size(20.dp)) {
                    drawArc(color = Color.Gray, startAngle = 0f, sweepAngle = 360f, useCenter = true, style = Stroke(width = 1.5.dp.toPx()))
                    drawArc(color = Color.Gray, startAngle = -90f, sweepAngle = 90f, useCenter = true)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        drawArc(
                            color = Color(0xFFF0F0F0),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 15.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = PrimaryNavy,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 15.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Strategic", fontSize = 10.sp, color = Color.Gray)
                        Text("${strategicPercentage.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(32.dp))
                Column {
                    LegendItem(PrimaryNavy, "${strategicPercentage.toInt()}% Strategic Growth", isSolid = true)
                    Spacer(modifier = Modifier.height(12.dp))
                    LegendItem(Color.Gray, "${bauPercentage.toInt()}% BAU Operations", isSolid = false)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String, isSolid: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isSolid) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        } else {
            Box(modifier = Modifier.size(8.dp).border(1.dp, color, CircleShape))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}
