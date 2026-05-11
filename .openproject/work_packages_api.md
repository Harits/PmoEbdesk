# OpenProject Work Packages API Guide

This guide provides instructions and examples for interacting with the OpenProject API, specifically focusing on Work Packages (tasks, features, bugs, etc.). This should be used by the Android Studio agent to sync data from the provided Excel/CSV files.

## Authentication

OpenProject uses Basic Authentication. You typically use `apikey` as the username and your actual API key as the password.

**Example Authorization Header:**
Authorization: Basic base64("apikey:YOUR_API_KEY")


## Endpoints

### 1. Get Projects
Before creating a Work Package, you may need to know the Project ID.

**Request:**
`GET /api/v3/projects`

**Response Example:**
```json
{
  "_type": "Collection",
  "total": 1,
  "count": 1,
  "_embedded": {
    "elements": [
      {
        "_type": "Project",
        "id": 1,
        "identifier": "demo-project",
        "name": "Demo Project"
      }
    ]
  }
}
2. Create a Work Package
To create a new Work Package, send a POST request.

Request: POST /api/v3/work_packages

Headers:

Content-Type: application/json
Authorization: Basic ...
Body Example:

{
  "subject": "New Task from Excel",
  "description": {
    "format": "markdown",
    "raw": "This task was synced from the provided data."
  },
  "_links": {
    "project": {
      "href": "/api/v3/projects/1"
    },
    "type": {
      "href": "/api/v3/types/1" 
    }
  }
}
Note: Type IDs typically map to 1=Task, 2=Feature, 3=Bug, but this can vary per OpenProject instance.

3. Update a Work Package
To update an existing Work Package, send a PATCH request.

Request: PATCH /api/v3/work_packages/{id}

Headers:

Content-Type: application/json
Authorization: Basic ...
Body Example (changing subject and adding a comment):

{
  "lockVersion": 1, 
  "subject": "Updated Task Subject",
  "journalNotes": {
    "format": "markdown",
    "raw": "Updated from sync script."
  }
}
Note: OpenProject uses optimistic locking. You often need to provide the lockVersion of the work package when updating it. You can retrieve this by fetching the work package first.

KMP (Kotlin Multiplatform) Ktor Example
If you are implementing this in Kotlin using Ktor, here is a snippet for creating a Work Package:

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.util.Base64

suspend fun createWorkPackage(
    host: String, 
    apiKey: String, 
    projectId: Int, 
    subject: String, 
    description: String
) {
    val client = HttpClient()
    val authString = "apikey:$apiKey"
    val encodedAuth = Base64.getEncoder().encodeToString(authString.toByteArray())

    val response: HttpResponse = client.post("$host/api/v3/work_packages") {
        header(HttpHeaders.Authorization, "Basic $encodedAuth")
        contentType(ContentType.Application.Json)
        setBody("""
            {
              "subject": "$subject",
              "description": {
                "format": "markdown",
                "raw": "$description"
              },
              "_links": {
                "project": {
                  "href": "/api/v3/projects/$projectId"
                }
              }
            }
        """.trimIndent())
    }
    
    println("Response status: ${response.status}")
    println("Response body: ${response.bodyAsText()}")
    client.close()
}
