package com.sekota.pmoebdesk.projects.data.repository

import com.sekota.pmoebdesk.projects.domain.model.ProjectStatus
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ProjectRepositoryTest {

    @Test
    fun testSearchProjects() = runTest {
        val mockEngine = MockEngine { request ->
            respond(
                content = """
                    {
                        "total": 1,
                        "count": 1,
                        "_embedded": {
                            "elements": [
                                {
                                    "id": 1,
                                    "identifier": "test-project",
                                    "name": "Test Project",
                                    "status": "On Track"
                                }
                            ]
                        }
                    }
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repository = ProductionProjectRepositoryImpl(client, "http://localhost", "api_key")
        val projects = repository.searchProjects("Test")

        assertEquals(1, projects.size)
        assertEquals("Test Project", projects[0].name)
        assertEquals(ProjectStatus.ON_TRACK, projects[0].status)
    }
}
