package com.sekota.pmoebdesk

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.fetch.Response

@Serializable
data class AppConfig(
    val OPENPROJECT_URL: String,
    val OPENPROJECT_API_KEY: String,
    val USE_MOCK_DATA: Boolean = false
)

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
suspend fun loadConfig(): AppConfig? {
    return try {
        val response = window.fetch("config.json").await<Response>()
        if (response.ok) {
            val jsonText = response.text().await<String>()
            Json { ignoreUnknownKeys = true }.decodeFromString<AppConfig>(jsonText)
        } else {
            null
        }
    } catch (e: Exception) {
        println("Failed to load config.json: \${e.message}")
        null
    }
}
