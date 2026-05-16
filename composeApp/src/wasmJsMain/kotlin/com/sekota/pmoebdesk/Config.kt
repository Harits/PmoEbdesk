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
    val USE_MOCK_DATA: Boolean = false,
    val PROJECT_PARENT_ID: Int? = null,
    val ALLOWED_PROJECT_IDS: String? = null
)

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
suspend fun loadConfig(): AppConfig? {
    return try {
        println("Fetching config.json...")
        val response = window.fetch("config.json").await<Response>()
        if (response.ok) {
            println("config.json fetched successfully, decoding...")
            // In Wasm, response.text() returns a Promise of JsString.
            // String is not a JsAny, so await<String>() might fail at runtime.
            val jsString = response.text().await<kotlin.js.JsString>()
            val jsonText = jsString.toString()
            Json { ignoreUnknownKeys = true }.decodeFromString<AppConfig>(jsonText).also {
                println("Config loaded: $it")
            }
        } else {
            println("Failed to fetch config.json: ${response.status} ${response.statusText}")
            null
        }
    } catch (e: Exception) {
        println("Error loading config.json: ${e.message}")
        e.printStackTrace()
        throw e // Rethrow to be caught in main.kt
    }
}
