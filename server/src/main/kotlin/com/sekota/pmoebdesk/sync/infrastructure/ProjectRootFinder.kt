package com.sekota.pmoebdesk.sync.infrastructure

import java.io.File

object ProjectRootFinder {
    fun findRoot(): File? {
        var current = File(".").absoluteFile
        while (current != null) {
            if (File(current, "gradlew").exists()) {
                return current
            }
            current = current.parentFile
        }
        return null
    }
}
