package com.sekota.pmoebdesk

import com.sekota.pmoebdesk.di.initKoin
import com.sekota.pmoebdesk.di.viewModelModule

import com.sekota.pmoebdesk.features.dashboard.domain.*
import com.sekota.pmoebdesk.features.dashboard.data.*

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    initKoin(useMockData = true, additionalModules = listOf(viewModelModule))

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "PmoEbdesk",
        ) {
            App(baseUrl = "http://localhost:8080", apiKey = "mock")
        }
    }
}
