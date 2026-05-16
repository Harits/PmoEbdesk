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
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.core.ui.PrimaryNavy
import com.sekota.pmoebdesk.core.ui.StatusRedBackground

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
