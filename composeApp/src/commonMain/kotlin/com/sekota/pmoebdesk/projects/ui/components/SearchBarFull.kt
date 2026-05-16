package com.sekota.pmoebdesk.projects.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.sekota.pmoebdesk.core.ui.OutlineColor
import com.sekota.pmoebdesk.core.ui.PrimaryNavy

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.SolidColor

@Composable
fun SearchBarFull(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
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
            
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text("Search projects...", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                    cursorBrush = SolidColor(PrimaryNavy),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }
}
