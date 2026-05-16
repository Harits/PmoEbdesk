package com.sekota.pmoebdesk.projects.domain.usecase

import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus
import com.sekota.pmoebdesk.projects.domain.repository.ProjectRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchProjectsUseCaseTest {

    private class MockProjectRepository : ProjectRepository {
        override suspend fun searchProjects(query: String?, parentId: Int?, allowedIds: List<Int>?, status: ProjectStatus?): List<Project> {
            val projects = listOf(
                Project(1, "p1", "Alpha", ProjectStatus.ON_TRACK),
                Project(2, "p2", "Beta", ProjectStatus.AT_RISK)
            )
            return projects.filter { 
                (query == null || it.name.contains(query)) &&
                (status == null || it.status == status)
            }
        }
    }

    @Test
    fun testUseCaseCallsRepository() = runTest {
        val repository = MockProjectRepository()
        val useCase = SearchProjectsUseCase(repository)
        
        val result = useCase("Alpha")
        
        assertEquals(1, result.size)
        assertEquals("Alpha", result[0].name)
    }

    @Test
    fun testUseCaseFiltersByStatus() = runTest {
        val repository = MockProjectRepository()
        val useCase = SearchProjectsUseCase(repository)
        
        val result = useCase(status = ProjectStatus.AT_RISK)
        
        assertEquals(1, result.size)
        assertEquals("Beta", result[0].name)
    }
}
