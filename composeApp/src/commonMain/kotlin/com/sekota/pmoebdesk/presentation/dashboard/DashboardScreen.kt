package com.sekota.pmoebdesk.presentation.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.domain.model.*

@Composable
fun DashboardContent(metrics: DashboardMetrics, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .background(MainBackground)
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            PortfolioHealthBanner()
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                StrategicProgressCard(
                    ragStatus = metrics.strategicRagStatus,
                    netProgress = metrics.netProgressPercentage,
                    modifier = Modifier.weight(1f)
                )
                ProgressMetricsCard(
                    strategicHours = metrics.strategicGrowthHours,
                    bauHours = metrics.businessAsUsualHours,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                CriticalPathTimelineCard(metrics.milestones, modifier = Modifier.weight(2f))
                RiskHeatmapCard(modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                RedListTableCard(metrics.exceptions, modifier = Modifier.weight(2f))
                BoardInterventionsSidebar(metrics.boardInterventions, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PortfolioHealthBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = StatusGreenText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text("Portfolio Health is GREEN", style = MaterialTheme.typography.headlineMedium, color = StatusGreenText)
                Text("All major strategic initiatives are tracking within 5% of baseline. No immediate systemic risks detected.", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            }
        }
    }
}

@Composable
fun StrategicProgressCard(ragStatus: RAGStatus, netProgress: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(280.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Strategic Goal RAG", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            val statusColor = when (ragStatus) {
                RAGStatus.GREEN -> StatusGreenText
                RAGStatus.AMBER -> StatusAmberText
                RAGStatus.RED -> StatusRedText
            }

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = statusColor,
                        startAngle = 135f,
                        sweepAngle = (270 * (netProgress / 100)).toFloat(),
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${netProgress.toInt()}%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text("Net Progress", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ProgressMetricsCard(strategicHours: Double, bauHours: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(280.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("Effort Distribution", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))

            val total = strategicHours + bauHours
            val strategicPercent = (strategicHours / total * 100).toInt()
            val bauPercent = (bauHours / total * 100).toInt()

            // Progress Bar
            Row(modifier = Modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(12.dp))) {
                Box(modifier = Modifier.weight(strategicPercent.toFloat()).fillMaxHeight().background(PrimaryNavy))
                Box(modifier = Modifier.weight(bauPercent.toFloat()).fillMaxHeight().background(Color(0xFF8B90FF)))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Legend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(PrimaryNavy, CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Strategic Growth", fontSize = 14.sp)
                    }
                    Text("${strategicHours.toInt()} hrs ($strategicPercent%)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(Color(0xFF8B90FF), CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Business As Usual", fontSize = 14.sp)
                    }
                    Text("${bauHours.toInt()} hrs ($bauPercent%)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp))
                }
            }
        }
    }
}

@Composable
fun CriticalPathTimelineCard(milestones: List<Milestone>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Critical Path Milestones", style = MaterialTheme.typography.titleLarge)
                Text("Q4 2023", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(48.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                // Timeline Line
                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFF1F3F4)))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    milestones.forEach { milestone ->
                        TimelinePoint(milestone.month, milestone.title, milestone.isCompleted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = Color(0xFFF1F3F4),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(20.dp).background(Color.Gray.copy(alpha = 0.3f), CircleShape))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Next milestone 'Market Entry' is currently tracking 2 days ahead of schedule.",
                        style = MaterialTheme.typography.bodyMedium
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

@Composable
fun RiskHeatmapCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("Risk Heatmap Matrix", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Text("High", fontSize = 10.sp, color = Color.Gray)
                    Text("Med", fontSize = 10.sp, color = Color.Gray)
                    Text("Low", fontSize = 10.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        GridMatrix()
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Low", fontSize = 10.sp, color = Color.Gray)
                        Text("Med", fontSize = 10.sp, color = Color.Gray)
                        Text("High", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun GridMatrix() {
    Column(modifier = Modifier.fillMaxSize()) {
        for (i in 0 until 3) {
            Row(modifier = Modifier.weight(1f)) {
                for (j in 0 until 4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(1.dp)
                            .background(
                                if (i == 0 && j == 3) Color(0xFFFFEBEE) else Color(0xFFF1F3F4)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (i == 0 && j == 3) {
                            Box(modifier = Modifier.size(24.dp).background(Color(0xFFC62828), CircleShape), contentAlignment = Alignment.Center) {
                                Text("3", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RedListTableCard(exceptions: List<ProjectException>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(400.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("The Red List: Immediate Attention", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA)).padding(12.dp)) {
                Text("Project Name", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                Text("Status", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                Text("Mitigation Summary", modifier = Modifier.weight(2f), fontSize = 12.sp, color = Color.Gray)
            }

            LazyColumn {
                items(exceptions) { ex ->
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(ex.projectName, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(4.dp)) {
                                Text("Critical", color = Color(0xFFC62828), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                            Text("Red", color = Color(0xFFC62828), fontSize = 10.sp)
                        }
                        Text(ex.mitigationSummary, modifier = Modifier.weight(2f), fontSize = 14.sp)
                    }
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                }
            }
        }
    }
}

@Composable
fun BoardInterventionsSidebar(interventions: List<BoardIntervention>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(400.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(24.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Board Interventions", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))

            interventions.forEachIndexed { index, intervention ->
                val type = if (index == 0) "Strategic Shift" else "Budget Approval"
                val icon = if (index == 0) "!" else "$\n"
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(24.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                                Text(icon, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(type, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            intervention.description,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Sign Decisions", color = PrimaryNavy, fontWeight = FontWeight.Bold)
            }
        }
    }
}
