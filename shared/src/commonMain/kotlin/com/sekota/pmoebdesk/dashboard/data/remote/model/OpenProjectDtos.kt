package com.sekota.pmoebdesk.dashboard.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkPackagesResponse(
    val total: Int,
    val count: Int,
    val _embedded: EmbeddedWorkPackages
)

@Serializable
data class EmbeddedWorkPackages(
    val elements: List<WorkPackageElement>
)

@Serializable
data class WorkPackageElement(
    val id: Int,
    val subject: String,
    val description: Description? = null,
    val percentageDone: Int? = null,
    val estimatedTime: String? = null,
    val startDate: String? = null,
    val dueDate: String? = null,
    @SerialName("_links")
    val _links: WorkPackageLinks? = null
)

@Serializable
data class WorkPackageLinks(
    val type: Link? = null,
    val status: Link? = null,
    val project: Link? = null
)

@Serializable
data class Link(
    val href: String,
    val title: String? = null
)

@Serializable
data class Description(
    val raw: String
)
