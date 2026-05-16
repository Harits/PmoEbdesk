package com.sekota.pmoebdesk.sync.data.remote

import com.sekota.pmoebdesk.sync.domain.model.WorkPackage

interface OpenProjectDataSource {
    suspend fun getProject(identifier: String): Int?
    suspend fun createProject(name: String, identifier: String): Int?
    suspend fun fetchWorkPackages(projectId: Int): List<WorkPackage>
    suspend fun createWorkPackage(workPackage: WorkPackage): Int?
    suspend fun updateWorkPackage(id: Int, lockVersion: Int, statusName: String?): Boolean
    suspend fun fetchStatuses(): Map<String, String>
}
