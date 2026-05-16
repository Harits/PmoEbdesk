package com.sekota.pmoebdesk.core

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform