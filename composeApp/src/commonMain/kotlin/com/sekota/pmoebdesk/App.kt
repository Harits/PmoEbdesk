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

@Composable
fun App(metrics: DashboardMetrics?) {
    MaterialTheme {
        if (metrics != null) {
            DashboardScreen(metrics = metrics)
        } else {
            Text("No metrics data available.")
        }
    }
}

@Composable
fun DashboardScreen(metrics: DashboardMetrics) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text("PMO Board of Directors Dashboard", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF1A237E))
        }

        // Tier 1: Executive Summary
        item {
            Text("Tier 1: Executive Summary", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                StatusCard(metrics.strategicRagStatus, modifier = Modifier.weight(1f))
                ProgressCard(metrics.netProgressPercentage, modifier = Modifier.weight(1f))
                EffortCard(metrics.strategicGrowthHours, metrics.businessAsUsualHours, modifier = Modifier.weight(1f))
            }
        }

        // Tier 2: Roadmap & Risks
        item {
            Text("Tier 2: Roadmap & Risks", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                MilestonesCard(metrics.milestones, modifier = Modifier.weight(1f))
                RisksCard(metrics.risks, modifier = Modifier.weight(1f))
            }
        }

        // Tier 3: Exceptions & Interventions
        item {
            Text("Tier 3: Action Items", style = MaterialTheme.typography.titleLarge)
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ExceptionsCard(metrics.exceptions, modifier = Modifier.weight(2f))
                InterventionsCard(metrics.boardInterventions, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatusCard(status: RAGStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        RAGStatus.GREEN -> Color(0xFF4CAF50)
        RAGStatus.AMBER -> Color(0xFFFFC107)
        RAGStatus.RED -> Color(0xFFF44336)
    }
    Card(modifier = modifier.height(150.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Status: \${status.name}", color = Color.White, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun ProgressCard(progress: Double, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(150.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Net Progress", style = MaterialTheme.typography.titleMedium)
            Text("\${progress.toInt()}%", style = MaterialTheme.typography.displayMedium, color = Color(0xFF1A237E))
            LinearProgressIndicator(progress = { (progress / 100).toFloat() }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun EffortCard(strategic: Double, bau: Double, modifier: Modifier = Modifier) {
    val total = strategic + bau
    val strPct = if (total > 0) ((strategic / total) * 100).toInt() else 0
    val bauPct = if (total > 0) ((bau / total) * 100).toInt() else 0
    Card(modifier = modifier.height(150.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Effort Distribution", style = MaterialTheme.typography.titleMedium)
            Text("Strategic: \${strPct}%", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF4CAF50))
            Text("BAU: \${bauPct}%", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF2196F3))
        }
    }
}

@Composable
fun MilestonesCard(milestones: List<Milestone>, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(250.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("Milestones", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
            milestones.forEach { milestone ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(milestone.title)
                    Text(milestone.date, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RisksCard(risks: List<Risk>, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(250.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("Top Risks (Prob x Impact)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
            risks.forEach { risk ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(risk.title)
                    Text("\${risk.probability} x \${risk.impact}", color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ExceptionsCard(exceptions: List<ProjectException>, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(250.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("The Red List", style = MaterialTheme.typography.titleMedium, color = Color.Red, modifier = Modifier.padding(bottom = 16.dp))
            exceptions.forEach { ex ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(ex.projectName, fontWeight = FontWeight.Bold)
                    Text(ex.mitigationSummary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun InterventionsCard(interventions: List<BoardIntervention>, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(250.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text("Board Interventions", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE65100), modifier = Modifier.padding(bottom = 16.dp))
            interventions.forEach { intervention ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("• ", fontWeight = FontWeight.Bold)
                    Text(intervention.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
