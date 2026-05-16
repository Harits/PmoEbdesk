package com.sekota.pmoebdesk

import com.sekota.pmoebdesk.sync.data.local.JvmCsvDataSource
import com.sekota.pmoebdesk.sync.data.remote.OpenProjectDataSourceImpl
import com.sekota.pmoebdesk.sync.data.repository.SyncRepositoryImpl
import com.sekota.pmoebdesk.sync.domain.usecase.SyncWorkPackagesUseCase
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File

fun loadEnv(path: String): Map<String, String> {
    val file = File(path)
    if (!file.exists()) return emptyMap()
    return file.readLines()
        .filter { it.isNotBlank() && !it.startsWith("#") }
        .associate {
            val parts = it.split("=", limit = 2)
            if (parts.size == 2) {
                parts[0].trim() to parts[1].trim()
            } else {
                parts[0].trim() to ""
            }
        }
}

fun main() = runBlocking {
    var rootDir = File("").absoluteFile
    while (rootDir != null && !File(rootDir, "gradlew").exists()) {
        rootDir = rootDir.parentFile
    }

    if (rootDir == null) {
        println("Could not find project root (gradlew)")
        return@runBlocking
    }

    val envFile = File(rootDir, ".openproject/.env")
    val env = loadEnv(envFile.absolutePath)
    val host = env["OPENPROJECT_HOST"] ?: "http://localhost:8080"
    val apiKey = env["OPENPROJECT_API_KEY"] ?: ""

    if (apiKey.isBlank()) {
        println("API Key not found in ${envFile.absolutePath}")
        return@runBlocking
    }

    val csvPath = env["CSV_FILE_PATH"] ?: ""
    val csvFile = if (File(csvPath).isAbsolute) File(csvPath) else File(rootDir, csvPath)
    
    if (!csvFile.exists()) {
        println("CSV file not found: ${csvFile.absolutePath}")
        return@runBlocking
    }

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                encodeDefaults = true
            })
        }
    }

    val csvDataSource = JvmCsvDataSource()
    val remoteDataSource = OpenProjectDataSourceImpl(client, host, apiKey)
    val repository = SyncRepositoryImpl(csvDataSource, remoteDataSource)
    val useCase = SyncWorkPackagesUseCase(repository)

    println("Starting sync from ${csvFile.absolutePath} to $host")
    try {
        useCase(csvFile.absolutePath)
        println("Sync completed successfully.")
    } catch (e: Exception) {
        println("Sync failed: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}
