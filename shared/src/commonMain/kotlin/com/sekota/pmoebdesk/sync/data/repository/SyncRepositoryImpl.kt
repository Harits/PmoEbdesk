package com.sekota.pmoebdesk.sync.data.repository

import com.sekota.pmoebdesk.sync.data.local.CsvDataSource
import com.sekota.pmoebdesk.sync.data.remote.OpenProjectDataSource
import com.sekota.pmoebdesk.sync.domain.model.WorkPackage
import com.sekota.pmoebdesk.sync.domain.repository.SyncRepository

class SyncRepositoryImpl(
    private val csvDataSource: CsvDataSource,
    private val remoteDataSource: OpenProjectDataSource
) : SyncRepository {

    override suspend fun getWorkPackagesFromCsv(filePath: String): List<WorkPackage> {
        val rows = csvDataSource.readCsv(filePath)
        return rows.flatMap { row ->
            row.tasks.map { task ->
                WorkPackage(
                    subject = task,
                    description = "Customer: ${row.customer}",
                    projectId = 0, 
                    startDate = formatDate(row.startDate),
                    dueDate = formatDate(row.finishDate),
                    customFields = mapOf("projectName" to row.projectName)
                )
            }
        }
    }

    override suspend fun createProjectIfNotExists(name: String, identifier: String): Int? {
        val existing = remoteDataSource.getProject(identifier)
        if (existing != null) return existing
        return remoteDataSource.createProject(name, identifier)
    }

    override suspend fun fetchExistingWorkPackages(projectId: Int): List<WorkPackage> {
        return remoteDataSource.fetchWorkPackages(projectId)
    }

    override suspend fun syncWorkPackage(workPackage: WorkPackage): Result<Unit> {
        val id = remoteDataSource.createWorkPackage(workPackage)
        return if (id != null) Result.success(Unit) else Result.failure(Exception("Failed to create work package"))
    }

    private fun formatDate(dateStr: String?): String? {
        if (dateStr.isNullOrBlank()) return null
        try {
            val parts = dateStr.split("-")
            if (parts.size != 3) return null
            val day = parts[0].padStart(2, '0')
            val month = when (parts[1].lowercase()) {
                "jan" -> "01"
                "feb" -> "02"
                "mar" -> "03"
                "apr" -> "04"
                "may" -> "05"
                "jun" -> "06"
                "jul" -> "07"
                "aug" -> "08"
                "sep" -> "09"
                "oct" -> "10"
                "nov" -> "11"
                "dec" -> "12"
                else -> return null
            }
            val year = parts[2]
            return "$year-$month-$day"
        } catch (e: Exception) {
            return null
        }
    }
}
