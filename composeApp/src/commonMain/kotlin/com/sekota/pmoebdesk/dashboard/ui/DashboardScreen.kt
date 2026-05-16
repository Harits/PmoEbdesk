package com.sekota.pmoebdesk.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sekota.pmoebdesk.dashboard.domain.model.DashboardMetrics
import com.sekota.pmoebdesk.dashboard.ui.components.*

@Composable
fun DashboardContent(metrics: DashboardMetrics, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            PortfolioHealthBanner(metrics.strategicRagStatus, metrics.trendPercentage)
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
