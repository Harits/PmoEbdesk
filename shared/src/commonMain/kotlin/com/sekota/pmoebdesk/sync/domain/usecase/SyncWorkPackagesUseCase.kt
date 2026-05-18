package com.sekota.pmoebdesk.sync.domain.usecase

import com.sekota.pmoebdesk.sync.domain.model.WorkPackage
import com.sekota.pmoebdesk.sync.domain.repository.SyncRepository
import kotlinx.datetime.*

class SyncWorkPackagesUseCase(
    private val repository: SyncRepository
) {
    suspend operator fun invoke(filePath: String) {
        println("  🔍 Reading CSV from: $filePath")
        val workPackages = repository.getWorkPackagesFromCsv(filePath)
        println("  📊 Found ${workPackages.size} tasks in CSV")

        val statusMap = repository.getStatusMap()
        val typeMap = repository.getTypeMap()
        
        val groupedByProject = workPackages.groupBy { it.customFields["projectName"] ?: "Default" }
        println("  📁 Grouped into ${groupedByProject.size} projects")
        
        groupedByProject.forEach { (projectName, packages) ->
            println("  🏗️ Processing project: $projectName")
            val projectIdentifier = slugify(projectName)
            val projectId = repository.createProjectIfNotExists(projectName, projectIdentifier)
            
            if (projectId != null) {
                println("  🆔 Project ID: $projectId")
                val existingPackages = repository.fetchExistingWorkPackages(projectId).associateBy { it.subject }
                println("  📥 Found ${existingPackages.size} existing work packages in OpenProject")
                
                packages.forEach { wp ->
                    // Map Type based on subject
                    val typeId = when {
                        wp.subject.contains("Milestone", ignoreCase = true) -> typeMap["milestone"]
                        wp.subject.contains("Risk", ignoreCase = true) -> typeMap["risk"]
                        else -> typeMap["task"]
                    }

                    // Map Status based on CSV metadata
                    val statusText = wp.customFields["statusKet"]?.lowercase() ?: ""
                    val statusId = when {
                        statusText.contains("done") -> statusMap["closed"]
                        statusText.contains("ngo") || statusText.contains("progress") -> statusMap["in progress"]
                        statusText.contains("stalled") || statusText.contains("overdue") -> statusMap["on hold"]
                        else -> statusMap["new"]
                    }

                    val updatedWp = wp.copy(
                        typeId = typeId,
                        statusId = statusId
                    )

                    val expanded = expandTask(updatedWp)
                    println("    📝 Task: '${wp.subject}' expanded to ${expanded.size} items")
                    expanded.forEach { task ->
                        if (!existingPackages.containsKey(task.subject)) {
                            println("    ➕ Creating new work package: '${task.subject}' (Type: ${task.typeId}, Status: ${task.statusId})")
                            val result = repository.syncWorkPackage(task.copy(projectId = projectId))
                            if (result.isFailure) {
                                println("    ❌ Failed to create '${task.subject}': ${result.exceptionOrNull()?.message}")
                            }
                        } else {
                            println("    ⏭️ Skipping '${task.subject}' (already exists)")
                        }
                    }
                }
            } else {
                println("  ⚠️ Could not get/create project ID for $projectName")
            }
        }
    }

    private fun expandTask(wp: WorkPackage): List<WorkPackage> {
        val subject = wp.subject
        if (!subject.contains("Monthly", ignoreCase = true)) return listOf(wp)

        val start = wp.startDate?.let { try { LocalDate.parse(it) } catch(e: Exception) { null } } ?: return listOf(wp)
        val end = wp.dueDate?.let { try { LocalDate.parse(it) } catch(e: Exception) { null } } ?: return listOf(wp)

        val tasks = mutableListOf<WorkPackage>()
        var current = LocalDate(start.year, start.month, 1)
        
        while (current <= end) {
            val taskStart = if (current < start) start else current
            // Move to next month first day, then back one day
            val nextMonth = if (current.monthNumber == 12) LocalDate(current.year + 1, 1, 1) else LocalDate(current.year, current.monthNumber + 1, 1)
            val lastDay = nextMonth.minus(1, DateTimeUnit.DAY)
            val taskEnd = if (lastDay > end) end else lastDay

            if (taskEnd >= taskStart) {
                val monthName = current.month.name.lowercase().replaceFirstChar { it.uppercase() }
                val taskSubject = "$subject - $monthName ${current.year}"
                
                tasks.add(wp.copy(
                    subject = taskSubject,
                    startDate = taskStart.toString(),
                    dueDate = taskEnd.toString()
                ))
            }
            current = nextMonth
        }
        return tasks
    }

    private fun slugify(text: String): String {
        return text.lowercase()
            .replace(Regex("[^a-z0-9]"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')
    }
}
