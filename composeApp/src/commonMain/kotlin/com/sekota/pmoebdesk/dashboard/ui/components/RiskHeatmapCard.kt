package com.sekota.pmoebdesk.dashboard.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.sekota.pmoebdesk.core.ui.*
import androidx.compose.ui.unit.sp


import com.sekota.pmoebdesk.dashboard.domain.model.Risk
import com.sekota.pmoebdesk.dashboard.domain.model.RiskLevel

@Composable
fun RiskHeatmapCard(risks: List<Risk>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(300.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Risk Heatmap Matrix", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy); Spacer(modifier = Modifier.width(8.dp)); InfoTooltip("Distribution of project risks by impact and likelihood.") }
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
                        GridMatrix(risks)
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
fun GridMatrix(risks: List<Risk>) {
    // Basic mapping: 3 rows (High, Med, Low), 4 columns
    // We'll just show the count of risks in each cell if possible, 
    // but for simplicity let's just show total risks in the top right for High/High
    val highRisks = risks.count { it.level == RiskLevel.HIGH }
    val medRisks = risks.count { it.level == RiskLevel.MEDIUM }
    
    Column(modifier = Modifier.fillMaxSize()) {
        for (i in 0 until 3) {
            Row(modifier = Modifier.weight(1f)) {
                for (j in 0 until 4) {
                    val isHotCell = (i == 0) && (j == 3)
                    val isMedCell = (i == 1) && (j == 2)
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(1.dp)
                            .background(
                                when {
                                    isHotCell && highRisks > 0 -> StatusRedBackground
                                    isMedCell && medRisks > 0 -> Color(0xFFFFF3E0)
                                    else -> Color(0xFFF1F3F4)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isHotCell && highRisks > 0) {
                            Box(modifier = Modifier.size(24.dp).background(Color(0xFFC62828), CircleShape), contentAlignment = Alignment.Center) {
                                Text(highRisks.toString(), color = Color.White, fontSize = 10.sp)
                            }
                        } else if (isMedCell && medRisks > 0) {
                            Box(modifier = Modifier.size(24.dp).background(Color(0xFFEF6C00), CircleShape), contentAlignment = Alignment.Center) {
                                Text(medRisks.toString(), color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
