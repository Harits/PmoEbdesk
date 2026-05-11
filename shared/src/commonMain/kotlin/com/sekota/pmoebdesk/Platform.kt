package com.sekota.pmoebdesk

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform