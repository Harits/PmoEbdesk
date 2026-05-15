package com.sekota.pmoebdesk.presentation.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sekota.pmoebdesk.domain.model.RAGStatus

data class ProjectItem(
    val name: String,
    val status: RAGStatus,
    val startDate: String,
    val endDate: String,
    val budget: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(modifier: Modifier = Modifier) {
    val projects = listOf(
        ProjectItem("Project Orion", RAGStatus.RED, "2023-01-10", "2023-12-15", "$1.2M"),
        ProjectItem("Nexus Integration", RAGStatus.AMBER, "2023-03-01", "2024-02-28", "$450k"),
        ProjectItem("Data Migration", RAGStatus.RED, "2023-05-15", "2023-11-30", "$800k"),
        ProjectItem("Cloud Infrastructure", RAGStatus.GREEN, "2023-02-20", "2024-06-30", "$2.5M"),
        ProjectItem("Security Audit", RAGStatus.GREEN, "2023-08-01", "2023-10-31", "$150k")
    )

    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {
        Text("Projects List", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF000666))
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search projects...") },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {},
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000666))
            ) {
                Text("Filter by Status", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Table Header
                Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8F9FA)).padding(12.dp)) {
                    Text("Project Name", modifier = Modifier.weight(2f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Status", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Start Date", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("End Date", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Budget", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }

                HorizontalDivider(color = Color(0xFFF0F0F0))

                LazyColumn {
                    items(projects.filter { it.name.contains(searchQuery, ignoreCase = true) }) { project ->
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(project.name, modifier = Modifier.weight(2f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Box(modifier = Modifier.weight(1f)) {
                                val statusColor = when (project.status) {
                                    RAGStatus.GREEN -> Color(0xFF2E7D32)
                                    RAGStatus.AMBER -> Color(0xFFF57C00)
                                    RAGStatus.RED -> Color(0xFFC62828)
                                }
                                val statusTextColor = when (project.status) {
                                    RAGStatus.GREEN -> Color(0xFFE8F5E9)
                                    RAGStatus.AMBER -> Color(0xFFFFF3E0)
                                    RAGStatus.RED -> Color(0xFFFFEBEE)
                                }
                                Surface(color = statusTextColor, shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        text = project.status.name,
                                        color = statusColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(project.startDate, modifier = Modifier.weight(1f), fontSize = 14.sp)
                            Text(project.endDate, modifier = Modifier.weight(1f), fontSize = 14.sp)
                            Text(project.budget, modifier = Modifier.weight(1f), fontSize = 14.sp)
                        }
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                }
            }
        }
    }
}
