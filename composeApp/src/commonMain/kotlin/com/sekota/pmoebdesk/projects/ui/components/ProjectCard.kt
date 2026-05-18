package com.sekota.pmoebdesk.projects.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.core.ui.*

@Composable
fun ProjectCard(
    name: String,
    status: String,
    statusColor: Color,
    statusBg: Color,
    deadline: String,
    startedDate: String,
    teamCount: Int = 0,
    isWarning: Boolean = false,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Status stripe
            Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(statusColor))
            
            Column(modifier = Modifier.padding(24.dp).weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(name, style = MaterialTheme.typography.headlineLarge, color = PrimaryNavy)
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(color = statusBg, shape = RoundedCornerShape(24.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(status, color = statusColor, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                    // More vert icon
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Box(modifier = Modifier.size(4.dp).background(Color.Gray, CircleShape))
                        Box(modifier = Modifier.size(4.dp).background(Color.Gray, CircleShape))
                        Box(modifier = Modifier.size(4.dp).background(Color.Gray, CircleShape))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("DEADLINE", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                        Text(deadline, style = MaterialTheme.typography.labelMedium, color = Color.Black)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F3F4))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Started: $startedDate", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    
                    if (isWarning) {
                        // Warning icon
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                moveTo(size.width / 2, 2.dp.toPx())
                                lineTo(size.width - 2.dp.toPx(), size.height - 2.dp.toPx())
                                lineTo(2.dp.toPx(), size.height - 2.dp.toPx())
                                close()
                            }
                            drawPath(path, color = StatusRed)
                            drawRect(color = Color.White, size = androidx.compose.ui.geometry.Size(2.dp.toPx(), 6.dp.toPx()), topLeft = androidx.compose.ui.geometry.Offset(center.x - 1.dp.toPx(), center.y - 1.dp.toPx()))
                            drawCircle(color = Color.White, radius = 1.dp.toPx(), center = center.copy(y = center.y + 7.dp.toPx()))
                        }
                    } else {
                        // Avatar stack placeholder
                        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                            repeat(minOf(teamCount, 3)) {
                                Box(modifier = Modifier.size(32.dp).border(2.dp, Color.White, CircleShape).clip(CircleShape).background(Color.LightGray))
                            }
                            if (teamCount > 3) {
                                Box(modifier = Modifier.size(32.dp).border(2.dp, Color.White, CircleShape).clip(CircleShape).background(PrimaryNavy), contentAlignment = Alignment.Center) {
                                    Text("+${teamCount - 3}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ProjectCardPreview() {
    DashboardTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProjectCard(
                name = "Project Orion",
                status = "On Track",
                statusColor = StatusGreen,
                statusBg = StatusGreenBackground,
                deadline = "Dec 2024",
                startedDate = "Jan 15, 2024",
                teamCount = 5,
                isWarning = false
            )
        }
    }
}

@Preview
@Composable
fun ProjectCardWarningPreview() {
    DashboardTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProjectCard(
                name = "Project Beta",
                status = "At Risk",
                statusColor = StatusAmber,
                statusBg = StatusAmberBackground,
                deadline = "Jun 2025",
                startedDate = "Mar 15, 2024",
                teamCount = 12,
                isWarning = true
            )
        }
    }
}
