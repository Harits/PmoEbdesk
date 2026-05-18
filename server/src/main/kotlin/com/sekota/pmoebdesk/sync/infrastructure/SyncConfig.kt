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
        val root = ProjectRootFinder.findRoot() ?: throw Exception("Could not find project root (gradlew missing)")
        
        // Try to load from root .env or .openproject/.env
        val envFile = File(root, ".env").takeIf { it.exists() } 
            ?: File(root, ".openproject/.env").takeIf { it.exists() }
            ?: throw Exception("Could not find .env file in project root or .openproject/ directory")

        println("Loading environment from: ${envFile.absolutePath}")
        val props = Properties()
        envFile.inputStream().use { props.load(it) }

        val host = props.getProperty("OPENPROJECT_HOST") ?: System.getenv("OPENPROJECT_HOST")
            ?: throw Exception("OPENPROJECT_HOST not found in .env or environment")
        
        val apiKey = props.getProperty("OPENPROJECT_API_KEY") ?: System.getenv("OPENPROJECT_API_KEY")
            ?: throw Exception("OPENPROJECT_API_KEY not found in .env or environment")

        val csvPath = props.getProperty("CSV_FILE_PATH") ?: System.getenv("CSV_FILE_PATH")
            ?: "data/projects.csv"

        val absoluteCsvPath = if (File(csvPath).isAbsolute) {
            csvPath
        } else {
            File(root, csvPath).absolutePath
        }

        val parentId = (props.getProperty("PROJECT_PARENT_ID") ?: System.getenv("PROJECT_PARENT_ID"))?.toIntOrNull()

        return SyncConfig(
            openProjectHost = host.trimEnd('/'),
            openProjectApiKey = apiKey,
            csvFilePath = absoluteCsvPath,
            projectParentId = parentId
        )
    }
}
