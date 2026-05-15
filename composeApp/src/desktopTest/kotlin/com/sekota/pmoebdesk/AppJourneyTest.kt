package com.sekota.pmoebdesk

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AppJourneyTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun journeyTest() {
        composeTestRule.setContent {
            App()
        }

        // Wait for data to load and verify Dashboard elements
        composeTestRule.waitUntil(10000) {
            composeTestRule.onAllNodes(androidx.compose.ui.test.hasText("Strategic Goal RAG")).fetchSemanticsNodes().isNotEmpty()
        }

        // Verify some dashboard elements are present
        composeTestRule.onNodeWithText("Strategic Goal RAG").assertExists()
        composeTestRule.onNodeWithText("Portfolio Health is GREEN").assertExists()
        composeTestRule.onNodeWithText("Sign Decisions").assertExists()

        // Navigate to Projects List
        composeTestRule.onNodeWithText("Projects List").performClick()

        // Verify Projects Screen elements are present
        composeTestRule.onNodeWithText("Search projects...").assertExists()
        composeTestRule.onNodeWithText("Filter by Status").assertExists()
        composeTestRule.onNodeWithText("Project Orion").assertExists()
        composeTestRule.onNodeWithText("Cloud Infrastructure").assertExists()
    }
}
