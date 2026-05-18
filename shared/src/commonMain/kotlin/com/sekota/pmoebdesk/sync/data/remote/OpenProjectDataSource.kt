package com.sekota.pmoebdesk.sync.data.remote

import com.sekota.pmoebdesk.sync.domain.model.WorkPackage

interface OpenProjectDataSource {
    suspend fun getProject(identifier: String): Int?
    suspend fun createProject(
        name: String, 
        identifier: String,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null,
        parentId: Int? = null
    ): Int?
    suspend fun updateProject(
        id: Int,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null,
        parentId: Int? = null
    ): Boolean
    suspend fun fetchWorkPackages(projectId: Int): List<WorkPackage>
    suspend fun createWorkPackage(workPackage: WorkPackage): Int?
    suspend fun updateWorkPackage(id: Int, lockVersion: Int, statusName: String?): Boolean
    suspend fun fetchStatuses(): Map<String, String>
    suspend fun fetchTypes(): Map<String, Int>
}
