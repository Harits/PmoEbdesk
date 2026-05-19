package com.sekota.pmoebdesk.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

import com.sekota.pmoebdesk.core.ui.*
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
