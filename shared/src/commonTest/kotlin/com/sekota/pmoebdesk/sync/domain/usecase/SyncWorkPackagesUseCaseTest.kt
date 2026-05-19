package com.sekota.pmoebdesk.sync.domain.usecase

import com.sekota.pmoebdesk.sync.domain.model.WorkPackage
import com.sekota.pmoebdesk.sync.domain.repository.SyncRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncWorkPackagesUseCaseTest {

    private open class MockSyncRepository : SyncRepository {
        var createProjectCalled = false
        var lastCreatedProjectStartDate: String? = null
        var lastCreatedProjectEndDate: String? = null
        var syncWorkPackageCalledCount = 0
        val syncedPackages = mutableListOf<WorkPackage>()

        override suspend fun getWorkPackagesFromCsv(filePath: String): List<WorkPackage> {
            return listOf(
                WorkPackage(
                    subject = "Monthly Report",
                    projectId = 0,
                    startDate = "2030-01-10",
                    dueDate = "2030-03-20",
                    percentageDone = 50,
                    estimatedTime = "PT8H",
                    customFields = mapOf("projectName" to "Test Project")
                )
            )
        }

        override suspend fun createProjectIfNotExists(
            name: String,
            identifier: String,
            startDate: String?,
            endDate: String?,
            status: String?,
            parentId: Int?
        ): Int? {
            createProjectCalled = true
            lastCreatedProjectStartDate = startDate
            lastCreatedProjectEndDate = endDate
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

        override suspend fun getStatusMap(): Map<String, Int> = mapOf(
            "closed" to 5,
            "in progress" to 2,
            "on hold" to 3,
            "new" to 1
        )
        override suspend fun getTypeMap(): Map<String, Int> = mapOf(
            "task" to 1,
            "milestone" to 2,
            "risk" to 3
        )
    }

    @Test
    fun testPastTaskClosing() = runTest {
        val repository = object : MockSyncRepository() {
            override suspend fun getWorkPackagesFromCsv(filePath: String): List<WorkPackage> {
                return listOf(
                    WorkPackage(
                        subject = "Past Task",
                        projectId = 0,
                        startDate = "2024-01-01",
                        dueDate = "2024-01-31",
                        percentageDone = 0,
                        customFields = mapOf("projectName" to "Past Project")
                    )
                )
            }
            override suspend fun getStatusMap(): Map<String, Int> = mapOf("closed" to 5)
        }
        val useCase = SyncWorkPackagesUseCase(repository)
        
        useCase("test.csv")
        
        assertEquals(1, repository.syncedPackages.size)
        val task = repository.syncedPackages[0]
        assertEquals(5, task.statusId, "Past task should have closed status")
        assertEquals(100, task.percentageDone, "Past task should be 100% complete")
    }

    @Test
    fun testMonthlyExpansionWithPastMonths() = runTest {
        val repository = object : MockSyncRepository() {
            override suspend fun getWorkPackagesFromCsv(filePath: String): List<WorkPackage> {
                return listOf(
                    WorkPackage(
                        subject = "Monthly Expansion Test",
                        projectId = 0,
                        startDate = "2024-12-01",
                        dueDate = "2026-12-31", // Project ends in future
                        percentageDone = 0,
                        customFields = mapOf("projectName" to "Future Project")
                    )
                )
            }
            override suspend fun getStatusMap(): Map<String, Int> = mapOf(
                "closed" to 5,
                "new" to 1
            )
        }
        val useCase = SyncWorkPackagesUseCase(repository)
        
        useCase("test.csv")
        
        // Find "December 2024" task
        val dec2024 = repository.syncedPackages.find { it.subject.contains("December 2024") }
        val jun2026 = repository.syncedPackages.find { it.subject.contains("June 2026") }
        
        assertTrue(dec2024 != null, "Dec 2024 task should exist")
        assertEquals(5, dec2024.statusId, "Past month should be closed")
        assertEquals(100, dec2024.percentageDone, "Past month should be 100% complete")
        
        assertTrue(jun2026 != null, "Jun 2026 task should exist")
        assertEquals(1, jun2026.statusId, "Future month should be new/in-progress")
        assertEquals(0, jun2026.percentageDone, "Future month should not be 100%")
    }

    @Test
    fun testMonthlyExpansion() = runTest {
        val repository = MockSyncRepository()
        val useCase = SyncWorkPackagesUseCase(repository)
        
        useCase("test.csv")
        
        assertTrue(repository.createProjectCalled)
        assertEquals("2030-01-10", repository.lastCreatedProjectStartDate)
        assertEquals("2030-03-20", repository.lastCreatedProjectEndDate)

        // Jan, Feb, Mar -> 3 tasks
        assertEquals(3, repository.syncWorkPackageCalledCount)
        assertEquals("Monthly Report - January 2030", repository.syncedPackages[0].subject)
        assertEquals("Monthly Report - February 2030", repository.syncedPackages[1].subject)
        assertEquals("Monthly Report - March 2030", repository.syncedPackages[2].subject)
        
        assertEquals("2030-01-10", repository.syncedPackages[0].startDate)
        assertEquals("2030-01-31", repository.syncedPackages[0].dueDate)
        
        assertEquals("2030-02-01", repository.syncedPackages[1].startDate)
        assertEquals("2030-02-28", repository.syncedPackages[1].dueDate)
        
        assertEquals("2030-03-01", repository.syncedPackages[2].startDate)
        assertEquals("2030-03-20", repository.syncedPackages[2].dueDate)

        assertEquals(50, repository.syncedPackages[0].percentageDone)
        assertEquals("PT8H", repository.syncedPackages[0].estimatedTime)
    }
}
