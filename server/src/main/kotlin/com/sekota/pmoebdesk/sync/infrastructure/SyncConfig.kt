package com.sekota.pmoebdesk.sync.infrastructure

import java.io.File
import java.util.Properties

data class SyncConfig(
    val openProjectHost: String,
    val openProjectApiKey: String,
    val csvFilePath: String,
    val projectParentId: Int? = null
)

object SyncConfigLoader {
    fun load(): SyncConfig {
        val props = Properties()
        
        // Try to load from root .env if it exists (local development)
        val root = ProjectRootFinder.findRoot()
        if (root != null) {
            val envFile = File(root, ".env").takeIf { it.exists() } 
                ?: File(root, ".openproject/.env").takeIf { it.exists() }
            
            if (envFile != null) {
                println("Loading environment from: ${envFile.absolutePath}")
                envFile.inputStream().use { props.load(it) }
            }
        }

        val host = System.getenv("OPENPROJECT_URL") ?: System.getenv("OPENPROJECT_HOST") 
            ?: props.getProperty("OPENPROJECT_URL") ?: props.getProperty("OPENPROJECT_HOST")
            ?: throw Exception("OPENPROJECT_URL or OPENPROJECT_HOST not found in environment or .env")
        
        val apiKey = System.getenv("OPENPROJECT_API_KEY") ?: props.getProperty("OPENPROJECT_API_KEY")
            ?: throw Exception("OPENPROJECT_API_KEY not found in environment or .env")

        val csvPath = System.getenv("CSV_FILE_PATH") ?: props.getProperty("CSV_FILE_PATH")
            ?: "data/projects.csv"

        val absoluteCsvPath = if (File(csvPath).isAbsolute) {
            csvPath
        } else {
            if (root != null) {
                File(root, csvPath).absolutePath
            } else {
                File(csvPath).absolutePath
            }
        }

        val parentId = (System.getenv("PROJECT_PARENT_ID") ?: props.getProperty("PROJECT_PARENT_ID"))?.toIntOrNull()

        return SyncConfig(
            openProjectHost = host.trimEnd('/'),
            openProjectApiKey = apiKey,
            csvFilePath = absoluteCsvPath,
            projectParentId = parentId
        )
    }
}

