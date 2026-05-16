package com.sekota.pmoebdesk.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            "© 2023 PMO Strategic Systems",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}
