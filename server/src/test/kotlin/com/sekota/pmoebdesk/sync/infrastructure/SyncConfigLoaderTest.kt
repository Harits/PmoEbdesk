package com.sekota.pmoebdesk.sync.infrastructure

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class SyncConfigLoaderTest {

    @Test
    fun testFindRoot() {
        val root = ProjectRootFinder.findRoot()
        assertTrue(root != null, "Should find project root containing gradlew")
        assertTrue(File(root, "gradlew").exists(), "Root should contain gradlew")
    }

    @Test
    fun testConfigLoading() {
        // This test might fail if no .env exists in the environment, 
        // but it verifies the logic can at least find the root and attempt loading.
        try {
            val config = SyncConfigLoader.load()
            assertTrue(config.openProjectHost.startsWith("http"), "Host should be a URL")
            assertTrue(config.openProjectApiKey.isNotBlank(), "API Key should not be blank")
        } catch (e: Exception) {
            // If .env is missing, we expect an exception with a specific message
            assertTrue(e.message?.contains(".env") == true || e.message?.contains("not found") == true)
        }
    }
}
