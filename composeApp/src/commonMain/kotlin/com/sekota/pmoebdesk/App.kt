package com.sekota.pmoebdesk

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.DESKTOP

// Stitch Design Tokens
val PrimaryNavy = Color(0xFF000666)
val BackgroundGray = Color(0xFFF8F9FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val OnSurfaceDark = Color(0xFF191C1D)
val OutlineVariant = Color(0xFFC6C5D4)

val StatusGreen = Color(0xFF4CAF50)
val StatusAmber = Color(0xFFFFC107)
val StatusRed = Color(0xFFBA1A1A)

@Composable
fun DashboardTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryNavy,
        onPrimary = Color.White,
        surface = SurfaceWhite,
        onSurface = OnSurfaceDark,
        background = BackgroundGray,
        onBackground = OnSurfaceDark,
        outlineVariant = OutlineVariant
    )

    val typography = Typography(
        displayLarge = MaterialTheme.typography.displayLarge.copy(
            fontSize = 57.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 40.sp,
            color = PrimaryNavy
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 28.sp
        ),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes(
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}

@Composable
fun App(metrics: DashboardMetrics?) {
    DashboardTheme {
        if (metrics != null) {
            DashboardScreen(metrics = metrics)
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun DashboardScreen(metrics: DashboardMetrics) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "PMO Board of Directors Dashboard",
                style = MaterialTheme.typography.headlineLarge
            )
        }

        // Tier 1: Executive Summary
        item {
            SectionHeader("Executive Summary")
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StatusCard(metrics.strategicRagStatus, modifier = Modifier.weight(1f))
                ProgressCard(metrics.netProgressPercentage, modifier = Modifier.weight(1f))
                EffortCard(metrics.strategicGrowthHours, metrics.businessAsUsualHours, modifier = Modifier.weight(1f))
            }
        }

        // Tier 2: Roadmap & Risks
        item {
            SectionHeader("Roadmap & Risks")
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MilestonesCard(metrics.milestones, modifier = Modifier.weight(1f))
                RisksCard(metrics.risks, modifier = Modifier.weight(1f))
            }
        }

        // Tier 3: Action Items
        item {
            SectionHeader("Action Items")
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ExceptionsCard(metrics.exceptions, modifier = Modifier.weight(2f))
                InterventionsCard(metrics.boardInterventions, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun StatusCard(status: RAGStatus, modifier: Modifier = Modifier) {
    val (color, label) = when (status) {
        RAGStatus.GREEN -> StatusGreen to "Healthy"
        RAGStatus.AMBER -> StatusAmber to "At Risk"
        RAGStatus.RED -> StatusRed to "Critical"
    }
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Strategic Health", style = MaterialTheme.typography.titleLarge)
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = label,
                    color = color,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ProgressCard(progress: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Net Progress", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "${progress.toInt()}%",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            LinearProgressIndicator(
                progress = { (progress / 100).toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun EffortCard(strategic: Double, bau: Double, modifier: Modifier = Modifier) {
    val total = strategic + bau
    val strPct = if (total > 0) ((strategic / total) * 100).toInt() else 0
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Effort Dist.", style = MaterialTheme.typography.titleLarge)
            Column {
                Text(
                    text = "$strPct% Strategic",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = StatusGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (strategic / total).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = StatusGreen,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun MilestonesCard(milestones: List<Milestone>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(280.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("Milestones", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            milestones.forEach { milestone ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(milestone.title, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = milestone.date,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RisksCard(risks: List<Risk>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(280.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("Critical Risks", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            risks.forEach { risk ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(risk.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    Surface(
                        color = StatusRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${risk.probability}x${risk.impact}",
                            color = StatusRed,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExceptionsCard(exceptions: List<ProjectException>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("The Red List", style = MaterialTheme.typography.titleLarge, color = StatusRed)
            Spacer(modifier = Modifier.height(16.dp))
            exceptions.forEach { ex ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(ex.projectName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(
                        text = ex.mitigationSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun InterventionsCard(interventions: List<BoardIntervention>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0).copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("Board Interventions", style = MaterialTheme.typography.titleLarge, color = Color(0xFFE65100))
            Spacer(modifier = Modifier.height(16.dp))
            interventions.forEach { intervention ->
                Row(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text("• ", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    Text(intervention.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Preview(device = DESKTOP)
@Composable
fun DashboardPreview() {
    val sampleMetrics = DashboardMetrics(
        strategicRagStatus = RAGStatus.AMBER,
        netProgressPercentage = 68.0,
        strategicGrowthHours = 1250.0,
        businessAsUsualHours = 450.0,
        milestones = listOf(
            Milestone("Cloud Migration Phase 1", "Oct 15"),
            Milestone("Security Audit", "Nov 02"),
            Milestone("Q4 Budget Approval", "Dec 10")
        ),
        risks = listOf(
            Risk("Resource Attrition", 4, 5),
            Risk("Dependency Delay", 3, 4)
        ),
        exceptions = listOf(
            ProjectException("Project Alpha", "Mitigation plan in progress. Expected recovery in 2 weeks."),
            ProjectException("Core Banking Sync", "Vendor dependency issue. Escalated to CTO.")
        ),
        boardInterventions = listOf(
            BoardIntervention("Approve additional headcount for Alpha"),
            BoardIntervention("Sign off on revised timeline for Sync")
        )
    )
    App(metrics = sampleMetrics)
}
