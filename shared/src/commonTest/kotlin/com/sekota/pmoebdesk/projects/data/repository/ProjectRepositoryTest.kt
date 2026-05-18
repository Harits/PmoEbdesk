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
    fun testSearchProjectsWithDates() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.contains("api/v3/projects") -> {
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
                                            "status": "On Track",
                                            "startDate": "2024-01-01",
                                            "endDate": "2024-12-31"
                                        }
                                    ]
                                }
                            }
                        """.trimIndent(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.contains("api/v3/work_packages") -> {
                    respond(
                        content = """
                            {
                                "total": 1,
                                "count": 1,
                                "_embedded": {
                                    "elements": [
                                        {
                                            "id": 101,
                                            "subject": "WP1",
                                            "startDate": "2024-02-01",
                                            "dueDate": "2024-11-30",
                                            "_links": {
                                                "project": { "href": "/api/v3/projects/1" }
                                            }
                                        }
                                    ]
                                }
                            }
                        """.trimIndent(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> respondError(HttpStatusCode.NotFound)
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val repository = ProductionProjectRepositoryImpl(client, "http://localhost", "api_key")
        val projects = repository.searchProjects("Test")

        assertEquals(1, projects.size)
        // Should prefer WP dates: 2024-02-01 to 2024-11-30
        assertEquals("2024-02-01", projects[0].startedDate)
        assertEquals("2024-11-30", projects[0].deadline)
    }
}
