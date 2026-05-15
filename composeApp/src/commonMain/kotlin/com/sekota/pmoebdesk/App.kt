package com.sekota.pmoebdesk

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Devices.DESKTOP

// Stitch Design Tokens
val PrimaryNavy = Color(0xFF000666)
val SidebarBackground = Color(0xFFECEEF4)
val MainBackground = Color(0xFFF8F9FB)
val SurfaceWhite = Color(0xFFFFFFFF)
val SelectedNav = Color(0xFFB4C5FF)
val OnSurfaceDark = Color(0xFF191C1D)
val OutlineVariant = Color(0xFFC6C5D4)

val StatusGreenText = Color(0xFF2E7D32)
val StatusRedText = Color(0xFFC62828)
val StatusAmberText = Color(0xFFF57C00)

@Composable
fun DashboardTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryNavy,
        onPrimary = Color.White,
        surface = SurfaceWhite,
        onSurface = OnSurfaceDark,
        background = MainBackground,
        onBackground = OnSurfaceDark,
        outlineVariant = OutlineVariant
    )

    val typography = Typography(
        displayLarge = MaterialTheme.typography.displayLarge.copy(
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy
        ),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryNavy
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        ),
        titleMedium = MaterialTheme.typography.titleMedium.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        ),
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(24.dp)
        ),
        content = content
    )
}

@Composable
fun App(metrics: DashboardMetrics?) {
    DashboardTheme {
        if (metrics != null) {
            MainLayout(metrics)
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun MainLayout(metrics: DashboardMetrics) {
    Row(modifier = Modifier.fillMaxSize()) {
        Sidebar(modifier = Modifier.width(260.dp))
        Column(modifier = Modifier.weight(1f)) {
            TopBar()
            DashboardContent(metrics, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun Sidebar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(SidebarBackground)
            .padding(24.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF8B90FF))
        Text("Portfolio Health", style = MaterialTheme.typography.titleLarge, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        NavItem("Executive Summary", isSelected = true)
        NavItem("Critical Path")
        NavItem("Risk Heatmap")
        NavItem("Exception List")
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Generate Board Report", color = Color.White, fontSize = 12.sp)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        NavItem("Settings")
        NavItem("Support")
    }
}

@Composable
fun NavItem(label: String, isSelected: Boolean = false) {
    Surface(
        color = if (isSelected) SelectedNav else Color.Transparent,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(20.dp).background(Color.Gray.copy(alpha = 0.3f), CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("PMO Strategic Oversight", style = MaterialTheme.typography.headlineLarge)
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Color(0xFFF1F3F4),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.width(300.dp).height(40.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(20.dp).background(Color.Gray.copy(alpha = 0.5f), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search projects...", color = Color.Gray, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
            Box(modifier = Modifier.size(24.dp).background(Color.Gray.copy(alpha = 0.3f), CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(24.dp).background(Color.Gray.copy(alpha = 0.3f), CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray)) {
                // Placeholder for profile image
            }
        }
    }
}

@Composable
fun DashboardContent(metrics: DashboardMetrics, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            PortfolioHealthBanner()
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                NetProgressCard(metrics.netProgressPercentage, modifier = Modifier.weight(1f))
                EffortDistributionCard(modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                CriticalPathRoadmapCard(modifier = Modifier.weight(1.5f))
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
                Box(modifier = Modifier.size(24.dp).background(Color(0xFF2E7D32), RoundedCornerShape(4.dp)))
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text("Portfolio Health", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text("Strategic RAG Status: Green", style = MaterialTheme.typography.headlineLarge)
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(16.dp).background(Color(0xFF2E7D32), RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("+4.2% Monthly Uplift", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun NetProgressCard(progress: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Net Progress", style = MaterialTheme.typography.titleLarge)
                Box(modifier = Modifier.size(24.dp).background(Color.Gray.copy(alpha = 0.2f), CircleShape))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${progress.toInt()}%", style = MaterialTheme.typography.displayLarge)
                Spacer(modifier = Modifier.width(8.dp))
                Text("of Year-to-Date Targets", style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            LinearProgressIndicator(
                progress = { (progress / 100).toFloat() },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                color = PrimaryNavy,
                trackColor = Color(0xFFF0F0F0),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun EffortDistributionCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Effort Distribution", style = MaterialTheme.typography.titleLarge)
                Box(modifier = Modifier.size(24.dp).background(Color.Gray.copy(alpha = 0.2f), CircleShape))
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
                            sweepAngle = 216f, // 60%
                            useCenter = false,
                            style = Stroke(width = 15.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ratio", fontSize = 10.sp, color = Color.Gray)
                        Text("3:2", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(32.dp))
                Column {
                    LegendItem(Color(0xFF000080), "60% Strategic Growth")
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem(Color.Gray.copy(alpha = 0.3f), "40% BAU Operations")
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp)
    }
}

@Composable
fun CriticalPathRoadmapCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("Critical Path Roadmap", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(32.dp))
            
            // Timeline
            Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                HorizontalDivider(modifier = Modifier.align(Alignment.Center).padding(horizontal = 40.dp), thickness = 1.dp, color = Color.LightGray)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    TimelinePoint("OCT", "Product Launch", true)
                    TimelinePoint("NOV", "Market Entry", true)
                    TimelinePoint("DEC", "Q3 Audit", false)
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

@Preview(device = DESKTOP)
@Composable
fun DashboardPreview() {
    val sampleMetrics = DashboardMetrics(
        strategicRagStatus = RAGStatus.GREEN,
        netProgressPercentage = 68.0,
        strategicGrowthHours = 1250.0,
        businessAsUsualHours = 450.0,
        milestones = listOf(
            Milestone("Product Launch", "OCT"),
            Milestone("Market Entry", "NOV"),
            Milestone("Q3 Audit", "DEC")
        ),
        risks = listOf(
            Risk("Resource Attrition", 4, 5),
            Risk("Dependency Delay", 3, 4)
        ),
        exceptions = listOf(
            ProjectException("Project Orion", "Hiring 2 senior architects to resolve technical bottleneck."),
            ProjectException("Nexus Integration", "Contractor dispute; legal mediation scheduled for Friday."),
            ProjectException("Data Migration", "Storage limits reached; approving emergency cloud burst.")
        ),
        boardInterventions = listOf(
            BoardIntervention("Approve shift of 3 devs from Project B to Project A to secure Q3 goal."),
            BoardIntervention("Authorize $50k contingency fund release for Orion licensing fees.")
        )
    )
    App(metrics = sampleMetrics)
}
