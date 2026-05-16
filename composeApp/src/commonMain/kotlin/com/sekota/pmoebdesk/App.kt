package com.sekota.pmoebdesk

import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.geometry.Offset


import com.sekota.pmoebdesk.features.dashboard.presentation.DashboardViewModel
import com.sekota.pmoebdesk.features.dashboard.presentation.DashboardState
import com.sekota.pmoebdesk.features.dashboard.domain.*
import com.sekota.pmoebdesk.features.projects.presentation.ProjectSearchViewModel
import com.sekota.pmoebdesk.features.projects.presentation.ProjectSearchState

val PrimaryNavy = Color(0xFF1A237E)
val MainBackground = Color(0xFFF8F9FA)
val StatusRed = Color(0xFFBA1A1A)
val StatusRedBackground = Color(0xFFFFDAD6)
val StatusAmber = Color(0xFFB77200)
val StatusAmberBackground = Color(0xFFFFE0B2)
val StatusGreen = Color(0xFF2E7D32)
val StatusGreenBackground = Color(0xFFC8E6C9)
val CardDarkNavy = Color(0xFF001E24)

@Composable
fun DashboardTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryNavy,
        background = MainBackground,
        surface = Color.White,
        error = StatusRed
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}

@Composable
fun App(
    viewModel: DashboardViewModel = koinViewModel(),
    projectSearchViewModel: ProjectSearchViewModel = koinViewModel(),
    baseUrl: String,
    apiKey: String
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard(baseUrl, apiKey)
    }

    DashboardTheme {
        var currentScreen by remember { mutableStateOf("dashboard") }
        
        when (val currentState = state) {
            is DashboardState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DashboardState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${currentState.message}", color = MaterialTheme.colorScheme.error)
                }
            }
            is DashboardState.Success -> {
                val metrics = currentState.metrics
                Column(modifier = Modifier.fillMaxSize().background(MainBackground)) {
                    TopBar(
                        onSearchFocus = { currentScreen = "search" },
                        onTitleClick = { currentScreen = "dashboard" }
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        if (currentScreen == "dashboard") {
                            DashboardContent(metrics, modifier = Modifier.fillMaxSize())
                        } else {
                            ProjectSearchScreen(projectSearchViewModel, baseUrl, apiKey, modifier = Modifier.fillMaxSize())
                        }
                    }
                    Footer()
                }
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
                // Mock logo
                Canvas(modifier = Modifier.size(24.dp)) {
                    drawRect(color = Color.White, topLeft = Offset(4.dp.toPx(), 4.dp.toPx()), size = androidx.compose.ui.geometry.Size(6.dp.toPx(), 16.dp.toPx()))
                    drawRect(color = Color.White.copy(alpha = 0.5f), topLeft = Offset(14.dp.toPx(), 10.dp.toPx()), size = androidx.compose.ui.geometry.Size(6.dp.toPx(), 10.dp.toPx()))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Executive Oversight System",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryNavy
            )
        }
        
        // Search bar mock for TopBar
        Surface(
            modifier = Modifier
                .width(300.dp)
                .height(40.dp)
                .clickable(onClick = onSearchFocus),
            shape = RoundedCornerShape(8.dp),
            color = MainBackground
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Canvas(modifier = Modifier.size(16.dp)) {
                    drawCircle(color = Color.Gray, radius = 5.dp.toPx(), center = Offset(6.dp.toPx(), 6.dp.toPx()), style = Stroke(width = 1.5.dp.toPx()))
                    drawLine(color = Color.Gray, start = Offset(10.dp.toPx(), 10.dp.toPx()), end = Offset(14.dp.toPx(), 14.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search projects...", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ProjectSearchScreen(viewModel: ProjectSearchViewModel, baseUrl: String, apiKey: String, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(baseUrl, apiKey)
    }

    LazyColumn(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SearchBarFull(query = searchQuery, onQueryChange = { viewModel.onQueryChanged(it) })
        }
        item {
            FilterBar(currentFilter = statusFilter, onFilterChange = { viewModel.onFilterChanged(it) })
        }

        when (val currentState = state) {
            is ProjectSearchState.Loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is ProjectSearchState.Error -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("Error: ${currentState.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            is ProjectSearchState.Success -> {
                if (currentState.projects.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            Text("No projects found.")
                        }
                    }
                } else {
                    items(currentState.projects.size) { index ->
                        val project = currentState.projects[index]
                        val statusColor = when(project.statusColorType) {
                            RAGStatus.GREEN -> StatusGreen
                            RAGStatus.AMBER -> StatusAmber
                            RAGStatus.RED -> StatusRed
                        }
                        val statusBg = when(project.statusColorType) {
                            RAGStatus.GREEN -> StatusGreenBackground
                            RAGStatus.AMBER -> StatusAmberBackground
                            RAGStatus.RED -> StatusRedBackground
                        }

                        ProjectCard(
                            name = project.name,
                            status = project.status,
                            statusColor = statusColor,
                            statusBg = statusBg,
                            budget = project.budget,
                            deadline = project.deadline,
                            startedDate = project.startedDate,
                            teamCount = project.teamCount,
                            isWarning = project.isWarning
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBarFull(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxSize()
        ) {
            Canvas(modifier = Modifier.size(20.dp)) {
                drawCircle(color = Color.Gray, radius = 6.dp.toPx(), center = Offset(8.dp.toPx(), 8.dp.toPx()), style = Stroke(width = 2.dp.toPx()))
                drawLine(color = Color.Gray, start = Offset(13.dp.toPx(), 13.dp.toPx()), end = Offset(18.dp.toPx(), 18.dp.toPx()), strokeWidth = 2.dp.toPx())
            }
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = PrimaryNavy),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text("Search projects...", color = Color.Gray, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )
        }
    }
}




@Composable
fun FilterBar(currentFilter: String, onFilterChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf("All", "On Track", "At Risk", "Critical")
                filters.forEach { filter ->
                    val isSelected = filter == currentFilter
                    Surface(
                        modifier = Modifier.clickable { onFilterChange(filter) },
                        color = if (isSelected) PrimaryNavy else Color.Transparent,
                        shape = RoundedCornerShape(24.dp),
                        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else Color.DarkGray,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sort by:", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Priority", color = PrimaryNavy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
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
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (isWarning) BorderStroke(1.dp, StatusRed.copy(alpha = 0.3f)) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(name, style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = statusBg,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            status,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(budget, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Budget", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Column {
                        Text("Deadline", fontSize = 12.sp, color = Color.Gray)
                        Text(deadline, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("Started", fontSize = 12.sp, color = Color.Gray)
                        Text(startedDate, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                if (teamCount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$teamCount Team Members", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    HorizontalDivider(color = Color(0xFFF1F4F8))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Project Details", fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Detailed description and metrics for $name would appear here. This section expands to show more granular data, recent updates, and specific risk factors.", fontSize = 14.sp, color = Color.DarkGray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View Full Dashboard")
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
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                NetProgressCard(metrics.netProgressPercentage, modifier = Modifier.weight(1f))
                EffortDistributionCard(modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                CriticalPathRoadmapCard(modifier = Modifier.weight(2f))
                RiskHeatmapCard(modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
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
        colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Strategic Portfolio Health", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Q3 Operational Overview", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Surface(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(12.dp)) {
                        drawCircle(color = StatusGreen)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("System Nominal", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun NetProgressCard(progress: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text("Net Progress", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$progress%", style = MaterialTheme.typography.displayLarge, color = PrimaryNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("+2.4% vs last month", color = StatusGreen, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            }
            LinearProgressIndicator(
                progress = { (progress / 100).toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = PrimaryNavy,
                trackColor = Color(0xFFF1F4F8)
            )
        }
    }
}

@Composable
fun EffortDistributionCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text("Effort Distribution", style = MaterialTheme.typography.titleMedium, color = Color.Gray)

            // Mock Donut Chart
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Canvas(modifier = Modifier.size(80.dp)) {
                    drawArc(color = PrimaryNavy, startAngle = -90f, sweepAngle = 240f, useCenter = false, style = Stroke(width = 16.dp.toPx()))
                    drawArc(color = StatusAmber, startAngle = 150f, sweepAngle = 120f, useCenter = false, style = Stroke(width = 16.dp.toPx()))
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column {
                    LegendItem(PrimaryNavy, "Strategic Growth (65%)", true)
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem(StatusAmber, "Business As Usual (35%)", false)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String, isSolid: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(12.dp)) {
            if (isSolid) drawCircle(color = color)
            else drawCircle(color = color, style = Stroke(width = 2.dp.toPx()))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = Color.DarkGray)
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
            Spacer(modifier = Modifier.height(24.dp))
            
            // Mock Timeline
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                    drawLine(color = Color(0xFFF1F4F8), start = Offset(0f, 0f), end = Offset(size.width, 0f), strokeWidth = 4.dp.toPx())
                    drawLine(color = PrimaryNavy, start = Offset(0f, 0f), end = Offset(size.width * 0.4f, 0f), strokeWidth = 4.dp.toPx())
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TimelinePoint("JUL", "Phase 1 Complete", true)
                    TimelinePoint("AUG", "Security Audit", true)
                    TimelinePoint("OCT", "Beta Launch", false)
                    TimelinePoint("DEC", "Q4 Milestone", false)
                }
            }
        }
    }
}

@Composable
fun TimelinePoint(month: String, title: String, isCompleted: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(month, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        Canvas(modifier = Modifier.size(16.dp)) {
            if (isCompleted) {
                drawCircle(color = PrimaryNavy)
            } else {
                drawCircle(color = Color.White)
                drawCircle(color = PrimaryNavy, style = Stroke(width = 2.dp.toPx()))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, color = if (isCompleted) PrimaryNavy else Color.Gray, fontWeight = FontWeight.SemiBold)
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
            Text("Risk Heatmap", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mock Heatmap Grid
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                GridMatrix()
                // Mock data points
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = StatusRed.copy(alpha = 0.8f), radius = 12.dp.toPx(), center = Offset(size.width * 0.8f, size.height * 0.2f))
                    drawCircle(color = StatusAmber.copy(alpha = 0.8f), radius = 16.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.4f))
                    drawCircle(color = StatusGreen.copy(alpha = 0.8f), radius = 8.dp.toPx(), center = Offset(size.width * 0.2f, size.height * 0.8f))
                }
                Text("Impact →", modifier = Modifier.align(Alignment.BottomEnd).offset(y = 16.dp), fontSize = 10.sp, color = Color.Gray)
                Text("↑ Probability", modifier = Modifier.align(Alignment.TopStart).offset(x = (-16).dp), fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun GridMatrix() {
    Column(modifier = Modifier.fillMaxSize()) {
        for (i in 0 until 5) {
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                for (j in 0 until 5) {
                    val color = when {
                        i < 2 && j > 2 -> StatusRedBackground
                        i > 2 && j < 2 -> StatusGreenBackground
                        else -> StatusAmberBackground.copy(alpha = 0.3f)
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(1.dp).background(color))
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
                items(exceptions.size) { index ->
                    val ex = exceptions[index]
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
                    drawLine(color = Color.White, start = Offset(0f, size.height * 0.8f), end = Offset(size.width, size.height * 0.2f), strokeWidth = 2.dp.toPx())
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(size.width * 0.2f, size.height * 0.65f))
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.5f))
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(size.width * 0.8f, size.height * 0.35f))
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
                                            topLeft = Offset(center.x - (rectSize.width / 2), center.y - 4.dp.toPx() - (rectSize.height / 2))
                                        )
                                        drawCircle(color = if (isSigned) Color.Gray else Color.White, radius = 1.5.dp.toPx(), center = center.copy(y = center.y + 6.dp.toPx()))
                                    }
                                } else {
                                    // Document/Code icon
                                    Canvas(modifier = Modifier.size(16.dp)) {
                                        drawRect(color = if (isSigned) Color.Gray else Color.White, style = Stroke(width = 1.5.dp.toPx()))
                                        drawLine(color = if (isSigned) Color.Gray else Color.White, start = Offset(4.dp.toPx(), 4.dp.toPx()), end = Offset(12.dp.toPx(), 12.dp.toPx()), strokeWidth = 1.5.dp.toPx())
                                        drawLine(color = if (isSigned) Color.Gray else Color.White, start = Offset(4.dp.toPx(), 12.dp.toPx()), end = Offset(12.dp.toPx(), 4.dp.toPx()), strokeWidth = 1.5.dp.toPx())
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


