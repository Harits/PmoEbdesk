package com.sekota.pmoebdesk.sync.domain.usecase

import com.sekota.pmoebdesk.sync.domain.model.WorkPackage
import com.sekota.pmoebdesk.sync.domain.repository.SyncRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncWorkPackagesUseCaseTest {

    private class MockSyncRepository : SyncRepository {
        var createProjectCalled = false
        var syncWorkPackageCalledCount = 0
        val syncedPackages = mutableListOf<WorkPackage>()

        override suspend fun getWorkPackagesFromCsv(filePath: String): List<WorkPackage> {
            return listOf(
                WorkPackage(
                    subject = "Monthly Report",
                    projectId = 0,
                    startDate = "2025-01-10",
                    dueDate = "2025-03-20",
                    customFields = mapOf("projectName" to "Test Project")
                )
            )
        }

        override suspend fun createProjectIfNotExists(name: String, identifier: String): Int? {
            createProjectCalled = true
            return 1
        }

        override suspend fun syncWorkPackage(workPackage: WorkPackage): Result<Unit> {
            syncWorkPackageCalledCount++
            syncedPackages.add(workPackage)
            return Result.success(Unit)
        }

        override suspend fun fetchExistingWorkPackages(projectId: Int): List<WorkPackage> {
            return emptyList()
        }
    }

    @Test
    fun testMonthlyExpansion() = runTest {
        val repository = MockSyncRepository()
        val useCase = SyncWorkPackagesUseCase(repository)
        
        useCase("test.csv")
        
        assertTrue(repository.createProjectCalled)
        // Jan, Feb, Mar -> 3 tasks
        assertEquals(3, repository.syncWorkPackageCalledCount)
        assertEquals("Monthly Report - January 2025", repository.syncedPackages[0].subject)
        assertEquals("Monthly Report - February 2025", repository.syncedPackages[1].subject)
        assertEquals("Monthly Report - March 2025", repository.syncedPackages[2].subject)
        
        assertEquals("2025-01-10", repository.syncedPackages[0].startDate)
        assertEquals("2025-01-31", repository.syncedPackages[0].dueDate)
        
        assertEquals("2025-02-01", repository.syncedPackages[1].startDate)
        assertEquals("2025-02-28", repository.syncedPackages[1].dueDate)
        
        assertEquals("2025-03-01", repository.syncedPackages[2].startDate)
        assertEquals("2025-03-20", repository.syncedPackages[2].dueDate)
    }
}
