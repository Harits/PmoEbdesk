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
val MainBackground = Color(0xFFF8F9FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val OnSurfaceDark = Color(0xFF191C1D)
val OutlineColor = Color(0xFF767683)
val OutlineVariant = Color(0xFFC6C5D4)
val ErrorColor = Color(0xFFBA1A1A)

val StatusGreen = Color(0xFF4CAF50)
val StatusGreenBackground = Color(0xFFE8F5E9)
val StatusAmber = Color(0xFFF57C00)
val StatusAmberBackground = Color(0xFFFFF3E0)
val StatusRed = Color(0xFFD32F2F)
val StatusRedBackground = Color(0xFFFFEBEE)
val CardDarkNavy = Color(0xFF000A3A)

@Composable
fun DashboardTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryNavy,
        onPrimary = Color.White,
        surface = SurfaceWhite,
        onSurface = OnSurfaceDark,
        background = MainBackground,
        onBackground = OnSurfaceDark,
        outlineVariant = OutlineVariant,
    )

    val typography = Typography(
        displayLarge = MaterialTheme.typography.displayLarge.copy(
            fontSize = 57.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryNavy,
            lineHeight = 40.sp
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
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp
        ),
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelMedium = MaterialTheme.typography.labelMedium.copy(
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ) // data-mono mapping
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes(
            small = RoundedCornerShape(4.dp), // sm
            medium = RoundedCornerShape(8.dp), // DEFAULT / medium
            large = RoundedCornerShape(16.dp)  // lg
        ),
        content = content
    )
}

@Composable
fun App(metrics: DashboardMetrics?) {
    DashboardTheme {
        var currentScreen by remember { mutableStateOf("dashboard") }
        
        if (metrics != null) {
            Column(modifier = Modifier.fillMaxSize().background(MainBackground)) {
                TopBar(
                    onSearchFocus = { currentScreen = "search" },
                    onTitleClick = { currentScreen = "dashboard" }
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (currentScreen == "dashboard") {
                        DashboardContent(metrics, modifier = Modifier.fillMaxSize())
                    } else {
                        ProjectSearchScreen(modifier = Modifier.fillMaxSize())
                    }
                }
                Footer()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            "© 2023 PMO Strategic Systems",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}


@Composable
fun TopBar(onSearchFocus: () -> Unit = {}, onTitleClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onTitleClick)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryNavy, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Simplified logo icon
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(modifier = Modifier.size(width = 20.dp, height = 2.dp).background(Color.White))
                    Box(modifier = Modifier.size(width = 14.dp, height = 2.dp).background(Color.White))
                    Box(modifier = Modifier.size(width = 20.dp, height = 2.dp).background(Color.White))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "PMO Strategic Oversight",
                style = MaterialTheme.typography.headlineLarge,
                color = PrimaryNavy,
                fontWeight = FontWeight.Bold
            )
        }
        
        Surface(
            color = Color(0xFFF1F3F4),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .width(360.dp)
                .height(44.dp)
                .clickable(onClick = onSearchFocus)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search icon placeholder
                Canvas(modifier = Modifier.size(18.dp)) {
                    drawCircle(color = Color.Gray, radius = size.minDimension / 3, style = Stroke(width = 2.dp.toPx()))
                    drawLine(color = Color.Gray, start = center, end = center * 1.8f, strokeWidth = 2.dp.toPx())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Search projects...", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ProjectSearchScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SearchBarFull()
        }
        item {
            FilterBar()
        }
        item {
            ProjectCard(
                name = "Project Orion",
                status = "On Track",
                statusColor = StatusGreen,
                statusBg = StatusGreenBackground,
                budget = "$1.2M",
                deadline = "Dec 2024",
                startedDate = "Jan 15, 2024",
                teamCount = 5
            )
        }
        item {
            ProjectCard(
                name = "Nexus Integration",
                status = "At Risk",
                statusColor = StatusAmber,
                statusBg = StatusAmberBackground,
                budget = "$850k",
                deadline = "Oct 2024",
                startedDate = "Mar 02, 2024",
                teamCount = 3
            )
        }
        item {
            ProjectCard(
                name = "Security Audit",
                status = "Critical",
                statusColor = StatusRed,
                statusBg = StatusRedBackground,
                budget = "$450k",
                deadline = "Aug 2024",
                startedDate = "Feb 12, 2024",
                isWarning = true
            )
        }
        item {
            ProjectCard(
                name = "Data Migration",
                status = "On Track",
                statusColor = StatusGreen,
                statusBg = StatusGreenBackground,
                budget = "$2.1M",
                deadline = "Mar 2025",
                startedDate = "May 20, 2024",
                teamCount = 1
            )
        }
    }
}

@Composable
fun SearchBarFull() {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp), // rounded-md (8px) per DESIGN.md text
        border = BorderStroke(1.dp, OutlineColor),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(20.dp)) {
                drawCircle(color = Color.Gray, radius = size.minDimension / 3, style = Stroke(width = 2.dp.toPx()))
                drawLine(color = Color.Gray, start = center, end = center * 1.8f, strokeWidth = 2.dp.toPx())
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Search projects...", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun FilterBar() {
    Surface(
        color = Color(0xFFF1F3F4),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Filter by Status", color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
            Canvas(modifier = Modifier.size(20.dp)) {
                drawLine(color = Color.Black, start = androidx.compose.ui.geometry.Offset(2.dp.toPx(), 6.dp.toPx()), end = androidx.compose.ui.geometry.Offset(18.dp.toPx(), 6.dp.toPx()), strokeWidth = 2.dp.toPx())
                drawLine(color = Color.Black, start = androidx.compose.ui.geometry.Offset(5.dp.toPx(), 10.dp.toPx()), end = androidx.compose.ui.geometry.Offset(15.dp.toPx(), 10.dp.toPx()), strokeWidth = 2.dp.toPx())
                drawLine(color = Color.Black, start = androidx.compose.ui.geometry.Offset(8.dp.toPx(), 14.dp.toPx()), end = androidx.compose.ui.geometry.Offset(12.dp.toPx(), 14.dp.toPx()), strokeWidth = 2.dp.toPx())
            }
        }
    }
}

@Composable
fun ProjectCard(
    name: String,
    status: String,
    statusColor: Color,
    statusBg: Color,
    budget: String,
    deadline: String,
    startedDate: String,
    teamCount: Int = 0,
    isWarning: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("BUDGET", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                        Text(budget, style = MaterialTheme.typography.labelMedium, color = Color.Black) // Using data-mono mapping
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("DEADLINE", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
                        Text(deadline, style = MaterialTheme.typography.labelMedium, color = Color.Black) // Using data-mono mapping
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
                // Checkmark icon
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
                    // Up arrow icon
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

@Composable
fun NetProgressCard(progress: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Net Progress", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
                // More vert icon placeholder
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(modifier = Modifier.size(4.dp).background(Color.Gray, CircleShape))
                    Box(modifier = Modifier.size(4.dp).background(Color.Gray, CircleShape))
                    Box(modifier = Modifier.size(4.dp).background(Color.Gray, CircleShape))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("${progress.toInt()}%", style = MaterialTheme.typography.displayLarge, color = PrimaryNavy)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Effort Distribution", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
                // Pie chart icon placeholder
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
                    LegendItem(PrimaryNavy, "60% Strategic Growth", isSolid = true)
                    Spacer(modifier = Modifier.height(12.dp))
                    LegendItem(Color.Gray, "40% BAU Operations", isSolid = false)
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

@Composable
fun CriticalPathRoadmapCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("Critical Path Roadmap", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
            Spacer(modifier = Modifier.height(32.dp))
            
            // Timeline
            Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                HorizontalDivider(modifier = Modifier.align(Alignment.Center).padding(horizontal = 40.dp), thickness = 1.dp, color = Color.LightGray)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    TimelinePoint("OCT", "Product Launch", isCompleted = true)
                    TimelinePoint("NOV", "Market Entry", isCompleted = true)
                    TimelinePoint("DEC", "Q3 Audit", isCompleted = false)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                color = Color(0xFFF1F4F8),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Info icon placeholder
                    Canvas(modifier = Modifier.size(20.dp)) {
                        drawCircle(color = PrimaryNavy, style = Stroke(width = 1.5.dp.toPx()))
                        drawCircle(color = PrimaryNavy, radius = 1.dp.toPx(), center = center.copy(y = center.y - 4.dp.toPx()))
                        drawLine(color = PrimaryNavy, start = center.copy(y = center.y - 1.dp.toPx()), end = center.copy(y = center.y + 5.dp.toPx()), strokeWidth = 2.dp.toPx())
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Next milestone 'Market Entry' is currently tracking 2 days ahead of schedule.",
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

@Composable
fun RiskHeatmapCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("Risk Heatmap Matrix", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxHeight().padding(vertical = 12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Text("High", fontSize = 10.sp, color = Color.Gray)
                    Text("Med", fontSize = 10.sp, color = Color.Gray)
                    Text("Low", fontSize = 10.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        GridMatrix()
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
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
                                if ((i == 0) && (j == 3)) StatusRedBackground else Color(0xFFF1F3F4)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if ((i == 0) && (j == 3)) {
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Text("The Red List: Immediate Attention", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F4F8)).padding(12.dp)) {
                Text("Project Name", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                Text("Status", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                Text("Mitigation Summary", modifier = Modifier.weight(2f), fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            }
            
            LazyColumn {
                items(exceptions) { ex ->
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(ex.projectName, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryNavy)
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(color = StatusRedBackground, shape = RoundedCornerShape(24.dp)) {
                                Text("Critical Red", color = StatusRed, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                            }
                        }
                        Text(ex.mitigationSummary, modifier = Modifier.weight(2f), fontSize = 14.sp, color = Color.DarkGray)
                    }
                    HorizontalDivider(color = Color(0xFFF1F4F8))
                }
            }
        }
    }
}

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
                // Board icon placeholder
                Canvas(modifier = Modifier.size(24.dp)) {
                    drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(0f, size.height * 0.8f), end = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.2f), strokeWidth = 2.dp.toPx())
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.65f))
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.5f))
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.35f))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Board Interventions", style = MaterialTheme.typography.titleLarge, color = Color.White)
                
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
                            // Item icon placeholder
                            Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                if (index == 0) {
                                    // Info/Exclamation icon
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
                                    // Document/Code icon
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

@Preview(device = DESKTOP)
@Composable
fun SearchPreview() {
    DashboardTheme {
        ProjectSearchScreen(modifier = Modifier.fillMaxSize().background(MainBackground))
    }
}
