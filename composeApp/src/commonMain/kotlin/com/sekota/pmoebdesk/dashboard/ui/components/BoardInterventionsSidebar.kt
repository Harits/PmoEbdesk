package com.sekota.pmoebdesk.dashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sekota.pmoebdesk.core.ui.*
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.dashboard.domain.model.BoardIntervention

@Composable
fun BoardInterventionsSidebar(interventions: List<BoardIntervention>, modifier: Modifier = Modifier) {
    var isSigned by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.height(400.dp),
        colors = CardDefaults.cardColors(containerColor = CardDarkNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BoardInterventionIcon(modifier = Modifier.size(24.dp), color = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) { Text("Board Interventions", style = MaterialTheme.typography.titleLarge, color = Color.White); Spacer(modifier = Modifier.width(8.dp)); InfoTooltip("Critical board-level decisions and interventions.", iconColor = Color.White) }
                
                if (isSigned) {
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(color = StatusGreen, shape = RoundedCornerShape(4.dp)) {
                        Text("SIGNED", color = Color.White, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            interventions.forEachIndexed { index, intervention ->
                val type = if (index == 0) "Strategic Shift" else "Budget Approval"
                Surface(
                    color = if (isSigned) Color.White.copy(alpha = 0.02f) else Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                if (index == 0) {
                                    Canvas(modifier = Modifier.size(16.dp)) {
                                        val rectSize = androidx.compose.ui.geometry.Size(2.dp.toPx(), 8.dp.toPx())
                                        drawRect(
                                            color = if (isSigned) Color.Gray else Color.White,
                                            size = rectSize,
                                            topLeft = androidx.compose.ui.geometry.Offset(center.x - (rectSize.width / 2), center.y - 4.dp.toPx() - (rectSize.height / 2))
                                        )
                                        drawCircle(color = if (isSigned) Color.Gray else Color.White, radius = 1.5.dp.toPx(), center = center.copy(y = center.y + 6.dp.toPx()))
                                    }
                                } else {
                                    Canvas(modifier = Modifier.size(16.dp)) {
                                        drawRect(color = if (isSigned) Color.Gray else Color.White, style = Stroke(width = 1.5.dp.toPx()))
                                        drawLine(color = if (isSigned) Color.Gray else Color.White, start = androidx.compose.ui.geometry.Offset(4.dp.toPx(), 4.dp.toPx()), end = androidx.compose.ui.geometry.Offset(12.dp.toPx(), 12.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                                        drawLine(color = if (isSigned) Color.Gray else Color.White, start = androidx.compose.ui.geometry.Offset(4.dp.toPx(), 12.dp.toPx()), end = androidx.compose.ui.geometry.Offset(12.dp.toPx(), 4.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(type, color = if (isSigned) Color.Gray else Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            intervention.description,
                            color = if (isSigned) Color.Gray else Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (!isSigned) {
                Button(
                    onClick = { isSigned = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Sign Decisions", color = CardDarkNavy, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedButton(
                    onClick = { isSigned = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Undo Sign", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BoardInterventionIcon(modifier: Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = color,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.15f, h * 0.15f),
            size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.45f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )

        val chartPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.25f, h * 0.45f)
            lineTo(w * 0.4f, h * 0.3f)
            lineTo(w * 0.55f, h * 0.4f)
            lineTo(w * 0.75f, h * 0.25f)
        }
        drawPath(
            path = chartPath,
            color = color,
            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
        )

        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.35f, h * 0.6f),
            end = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.85f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.6f),
            end = androidx.compose.ui.geometry.Offset(w * 0.75f, h * 0.85f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawCircle(color = color, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w * 0.35f, h * 0.75f))
        drawCircle(color = color, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.75f))
        drawCircle(color = color, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.75f))
    }
}
