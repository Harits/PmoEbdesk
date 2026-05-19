package com.sekota.pmoebdesk

import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.*
import java.io.File

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: SERVER_PORT
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val opUrl = (System.getenv("OPENPROJECT_URL") ?: System.getenv("OPENPROJECT_HOST") ?: "").trimEnd('/')
    val proxyClient = if (opUrl.isNotEmpty()) HttpClient(CIO) else null

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        }, contentType = ContentType.Application.Json)
    }

    routing {
        // Health check
        get("/health") {
            call.respond(mapOf("status" to "OK"))
        }

        // Dynamic config.json
        get("/config.json") {
            val config = buildJsonObject {
                // Return the actual host URL so the UI logic doesn't see an empty string
                // But the UI will still use /api because we are serving from the same host
                put("OPENPROJECT_URL", opUrl)
                put("OPENPROJECT_API_KEY", System.getenv("OPENPROJECT_API_KEY") ?: "")
                put("USE_MOCK_DATA", System.getenv("USE_MOCK_DATA")?.toBoolean() ?: false)
                put("PROJECT_PARENT_ID", System.getenv("PROJECT_PARENT_ID")?.toIntOrNull())
                put("ALLOWED_PROJECT_IDS", System.getenv("ALLOWED_PROJECT_IDS"))
            }
            call.respond(config)
        }

        // API Proxy to OpenProject (avoids CORS issues)
        if (opUrl.isNotEmpty() && proxyClient != null) {
            route("/api") {
                get("{...}") {
                    val fullUri = call.request.uri
                    val apiPath = fullUri.removePrefix("/api")
                    val targetUrl = "$opUrl/api$apiPath"
                    
                    println("DEBUG: Proxying GET to OpenProject: $targetUrl")
                    try {
                        val response = proxyClient.get(targetUrl) {
                            headers {
                                call.request.headers.forEach { name, values ->
                                    // Forward essential headers, skip hop-by-hop
                                    if (name.lowercase() !in listOf("host", "content-length", "content-type", "connection")) {
                                        values.forEach { append(name, it) }
                                    }
                                }
                            }
                        }
                        
                        val responseBody = response.readRawBytes()
                        println("DEBUG: Proxy received ${response.status} (${responseBody.size} bytes)")
                        
                        call.respondBytes(
                            responseBody,
                            response.contentType(),
                            response.status
                        )
                    } catch (e: Exception) {
                        println("ERROR: Proxy failed for $targetUrl: ${e.message}")
                        e.printStackTrace()
                        call.respond(HttpStatusCode.BadGateway, mapOf("error" to (e.message ?: "Unknown proxy error")))
                    }
                }
            }
        }

        // Serve Dashboard UI (WasmJS)
        val wwwDir = (File("/app/www").takeIf { it.exists() } ?: File("www")).absoluteFile
        println("DEBUG: Static files directory: ${wwwDir.absolutePath}")
        
        // Manual Static & SPA Routing
        get("{route...}") {
            val path = call.request.path().trimStart('/')
            val requestedFile = if (path.isEmpty()) File(wwwDir, "index.html") else File(wwwDir, path)
            
            if (requestedFile.exists() && requestedFile.isFile) {
                call.respondFile(requestedFile)
            } else if (path.isEmpty() || !path.contains(".") || path.endsWith(".html")) {
                val indexFile = File(wwwDir, "index.html")
                if (indexFile.exists()) {
                    call.respondFile(indexFile)
                } else {
                    call.respond(HttpStatusCode.NotFound, "index.html not found")
                }
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
