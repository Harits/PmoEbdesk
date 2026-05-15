package com.sekota.pmoebdesk.presentation.dashboard

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.NavDestination

@Composable
fun Sidebar(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination,
    onNavigate: (NavDestination) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(SidebarBackground)
            .padding(24.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF8B90FF))
        Text("Portfolio Health", style = MaterialTheme.typography.titleLarge, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        NavItem(
            label = "Executive Summary",
            isSelected = currentDestination == NavDestination.DASHBOARD,
            onClick = { onNavigate(NavDestination.DASHBOARD) }
        )
        NavItem(
            label = "Projects List",
            isSelected = currentDestination == NavDestination.PROJECTS,
            onClick = { onNavigate(NavDestination.PROJECTS) }
        )
        NavItem("Critical Path")
        NavItem("Risk Heatmap")
        NavItem("Exception List")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Generate Board Report", color = Color.White, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        NavItem("Settings")
        NavItem("Support")
    }
}

@Composable
fun NavItem(label: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    Surface(
        color = if (isSelected) SelectedNav else Color.Transparent,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(20.dp).background(Color.Gray.copy(alpha = 0.3f), CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) PrimaryNavy else OnSurfaceDark
            )
        }
    }
}

@Composable
fun TopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("PMO Executive Board", style = MaterialTheme.typography.headlineMedium, color = PrimaryNavy)
            Text("Data as of Oct 24, 2023 - 09:00 AM UTC", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFF1F3F4), CircleShape), contentAlignment = Alignment.Center) {
                Text("?", color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFF1F3F4), CircleShape), contentAlignment = Alignment.Center) {
                Text("B", color = Color.Gray) // Bell icon placeholder
            }
            Spacer(modifier = Modifier.width(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(PrimaryNavy, CircleShape), contentAlignment = Alignment.Center) {
                    Text("JS", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Jane Smith", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Board Member", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}
