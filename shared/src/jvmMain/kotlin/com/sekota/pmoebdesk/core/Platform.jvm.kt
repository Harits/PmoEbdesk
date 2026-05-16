package com.sekota.pmoebdesk.core

import com.sekota.pmoebdesk.core.Platform

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()