package com.sekota.pmoebdesk.sync.domain.repository

import com.sekota.pmoebdesk.sync.domain.model.WorkPackage

interface SyncRepository {
    suspend fun getWorkPackagesFromCsv(filePath: String): List<WorkPackage>
    suspend fun createProjectIfNotExists(
        name: String, 
        identifier: String,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null,
        parentId: Int? = null
    ): Int?
    suspend fun syncWorkPackage(workPackage: WorkPackage): Result<Unit>
    suspend fun fetchExistingWorkPackages(projectId: Int): List<WorkPackage>
    suspend fun getStatusMap(): Map<String, Int>
    suspend fun getTypeMap(): Map<String, Int>
}
