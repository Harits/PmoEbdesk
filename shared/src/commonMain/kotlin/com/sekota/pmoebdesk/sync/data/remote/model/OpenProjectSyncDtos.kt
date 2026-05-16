package com.sekota.pmoebdesk.sync.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectPayload(
    val name: String,
    val identifier: String,
    val description: DescriptionPayload = DescriptionPayload(raw = ""),
    val public: Boolean = false
)

@Serializable
data class DescriptionPayload(
    val format: String = "markdown",
    val raw: String
)

@Serializable
data class WorkPackagePayload(
    val lockVersion: Int? = null,
    val subject: String? = null,
    val description: DescriptionPayload? = null,
    val startDate: String? = null,
    val dueDate: String? = null,
    val _links: WorkPackageLinks? = null
)

@Serializable
data class WorkPackageLinks(
    val project: Link,
    val type: Link? = null,
    val status: Link? = null
)

@Serializable
data class Link(
    val href: String
)
