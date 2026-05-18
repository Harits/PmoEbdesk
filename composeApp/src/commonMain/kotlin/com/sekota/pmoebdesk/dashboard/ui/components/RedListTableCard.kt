package com.sekota.pmoebdesk.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.core.ui.*
import com.sekota.pmoebdesk.dashboard.domain.model.ProjectException

@Composable
fun RedListTableCard(exceptions: List<ProjectException>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(400.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("The Red List: Immediate Attention", style = MaterialTheme.typography.titleLarge, color = PrimaryNavy)
                Spacer(modifier = Modifier.width(8.dp))
                InfoTooltip("Projects requiring immediate management attention.")
            }
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
