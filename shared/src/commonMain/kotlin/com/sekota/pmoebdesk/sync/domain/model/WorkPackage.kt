package com.sekota.pmoebdesk.sync.domain.model

data class WorkPackage(
    val id: String? = null,
    val subject: String,
    val description: String? = null,
    val projectId: Int,
    val typeId: Int? = null,
    val statusId: Int? = null,
    val startDate: String? = null,
    val dueDate: String? = null,
    val estimatedTime: String? = null,
    val lockVersion: Int? = null,
    val customFields: Map<String, String> = emptyMap()
)
