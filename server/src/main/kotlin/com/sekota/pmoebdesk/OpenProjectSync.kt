package com.sekota.pmoebdesk

import com.sekota.pmoebdesk.sync.data.local.JvmCsvDataSource
import com.sekota.pmoebdesk.sync.data.remote.OpenProjectDataSourceImpl
import com.sekota.pmoebdesk.sync.data.repository.SyncRepositoryImpl
import com.sekota.pmoebdesk.sync.domain.usecase.SyncWorkPackagesUseCase
import com.sekota.pmoebdesk.sync.infrastructure.SyncConfigLoader
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

/**
 * Standalone synchronization tool to sync work packages from CSV/Excel into OpenProject.
 * Adheres to Clean Architecture and Screaming Architecture principles.
 */
fun main() = runBlocking {
    val config = try {
        SyncConfigLoader.load()
    } catch (e: Exception) {
        println("Configuration Error: ${e.message}")
        return@runBlocking
    }

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = false
            })
        }
    }

    val csvDataSource = JvmCsvDataSource()
    val remoteDataSource = OpenProjectDataSourceImpl(
        client = client,
        host = config.openProjectHost,
        apiKey = config.openProjectApiKey
    )
    val repository = SyncRepositoryImpl(csvDataSource, remoteDataSource)
    val useCase = SyncWorkPackagesUseCase(repository)

    println("🚀 Starting OpenProject Synchronization")
    println("📍 Source CSV: ${config.csvFilePath}")
    println("🌐 Target Host: ${config.openProjectHost}")

    try {
        useCase(config.csvFilePath, config.projectParentId)
        println("✅ Synchronization completed successfully.")
    } catch (e: Exception) {
        println("❌ Synchronization failed: ${e.message}")
        e.printStackTrace()
    } finally {
        client.close()
    }
}
