package com.sekota.pmoebdesk.projects.domain.usecase

import com.sekota.pmoebdesk.projects.domain.model.Project
import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus
import com.sekota.pmoebdesk.projects.domain.repository.ProjectRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchProjectsUseCaseTest {

    private class MockProjectRepository : ProjectRepository {
        override suspend fun searchProjects(query: String?, parentId: Int?, allowedIds: List<Int>?): List<Project> {
            return listOf(
                Project(1, "p1", "Alpha", ProjectStatus.ON_TRACK)
            )
        }
    }

    @Test
    fun testUseCaseCallsRepository() = runTest {
        val repository = MockProjectRepository()
        val useCase = SearchProjectsUseCase(repository)
        
        val result = useCase("query")
        
        assertEquals(1, result.size)
        assertEquals("Alpha", result[0].name)
    }
}
