package com.sekota.pmoebdesk.sync.domain.usecase

import com.sekota.pmoebdesk.sync.domain.model.WorkPackage
import com.sekota.pmoebdesk.sync.domain.repository.SyncRepository
import kotlinx.datetime.*

class SyncWorkPackagesUseCase(
    private val repository: SyncRepository
) {
    suspend operator fun invoke(filePath: String) {
        val workPackages = repository.getWorkPackagesFromCsv(filePath)
        
        val groupedByProject = workPackages.groupBy { it.customFields["projectName"] ?: "Default" }
        
        groupedByProject.forEach { (projectName, packages) ->
            val projectIdentifier = slugify(projectName)
            val projectId = repository.createProjectIfNotExists(projectName, projectIdentifier)
            
            if (projectId != null) {
                val existingPackages = repository.fetchExistingWorkPackages(projectId).associateBy { it.subject }
                
                packages.forEach { wp ->
                    val expanded = expandTask(wp)
                    expanded.forEach { task ->
                        if (!existingPackages.containsKey(task.subject)) {
                            repository.syncWorkPackage(task.copy(projectId = projectId))
                        } else {
                            // TODO: Optional update if needed
                        }
                    }
                }
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
