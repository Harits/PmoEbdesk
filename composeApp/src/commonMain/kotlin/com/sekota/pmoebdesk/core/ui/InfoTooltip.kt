package com.sekota.pmoebdesk.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTooltip(text: String, iconColor: Color = Color.Gray) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(text, modifier = Modifier.padding(8.dp))
            }
        },
        state = rememberTooltipState()
    ) {
        Canvas(modifier = Modifier.size(16.dp)) {
            drawCircle(color = iconColor, style = Stroke(width = 1.5.dp.toPx()))
            // Draw 'i'
            drawLine(
                color = iconColor,
                start = center.copy(y = center.y - 1.dp.toPx()),
                end = center.copy(y = center.y + 4.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
            drawCircle(
                color = iconColor,
                radius = 1.dp.toPx(),
                center = center.copy(y = center.y - 4.dp.toPx())
            )
        }
    }
}
