package com.sekota.pmoebdesk.projects.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
