package com.sekota.pmoebdesk.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices.DESKTOP
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar(
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearchFocus: () -> Unit = {},
    onTitleClick: () -> Unit = {},
    onProjectSelectorClick: () -> Unit = {},
    selectedProjectName: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                onClick = onTitleClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
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
            Text(
                "PMO Strategic Oversight",
                style = MaterialTheme.typography.headlineLarge,
                color = PrimaryNavy,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(32.dp))
        
        // Vertical Separator
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(OutlineVariant)
        )

        Spacer(modifier = Modifier.width(32.dp))

        // Project Selector
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    onClick = onProjectSelectorClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
                .padding(vertical = 8.dp)
        ) {
            Column {
                Text(
                    "Selected Project",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        selectedProjectName ?: "All Projects",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PrimaryNavy,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Change project",
                        tint = PrimaryNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()
        val borderColor = if (isFocused) PrimaryNavy.copy(alpha = 0.5f) else Color.Transparent

        Surface(
            color = if (isFocused) Color.White else Color(0xFFF1F3F4),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .width(360.dp)
                .height(44.dp)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    val iconColor = if (isFocused) PrimaryNavy else Color.Gray
                    val strokeWidth = 2.dp.toPx()
                    val radius = size.minDimension / 3
                    val circleCenter = Offset(radius + strokeWidth, radius + strokeWidth)

                    drawCircle(color = iconColor, radius = radius, center = circleCenter, style = Stroke(width = strokeWidth))

                    val startX = circleCenter.x + (radius * 0.707f)
                    val startY = circleCenter.y + (radius * 0.707f)

                    drawLine(color = iconColor, start = Offset(startX, startY), end = Offset(size.width, size.height), strokeWidth = strokeWidth)
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
                        singleLine = true,
                        interactionSource = interactionSource
                    )
                }

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = DESKTOP)
@Composable
fun TopBarPreview() {
    DashboardTheme {
        TopBar()
    }
}

@Preview(device = DESKTOP)
@Composable
fun TopBarWithProjectPreview() {
    DashboardTheme {
        TopBar(
            query = "Search query",
            selectedProjectName = "Project Orion"
        )
    }
}
