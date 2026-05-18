package com.sekota.pmoebdesk.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar(
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearchFocus: () -> Unit = {},
    onTitleClick: () -> Unit = {},
    selectedProjectName: String? = null,
) {
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
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(modifier = Modifier.size(width = 20.dp, height = 2.dp).background(Color.White))
                    Box(modifier = Modifier.size(width = 14.dp, height = 2.dp).background(Color.White))
                    Box(modifier = Modifier.size(width = 20.dp, height = 2.dp).background(Color.White))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "PMO Strategic Oversight",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PrimaryNavy,
                    fontWeight = FontWeight.Bold
                )
                if (selectedProjectName != null) {
                    Text(
                        selectedProjectName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
        
        Surface(
            color = Color(0xFFF1F3F4),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .width(360.dp)
                .height(44.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    drawCircle(color = Color.Gray, radius = size.minDimension / 3, style = Stroke(width = 2.dp.toPx()))
                    drawLine(color = Color.Gray, start = center, end = center * 1.8f, strokeWidth = 2.dp.toPx())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text("Search projects...", color = Color.Gray, fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = {
                            onQueryChange(it)
                            onSearchFocus()
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                        cursorBrush = SolidColor(PrimaryNavy),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }
    }
}
