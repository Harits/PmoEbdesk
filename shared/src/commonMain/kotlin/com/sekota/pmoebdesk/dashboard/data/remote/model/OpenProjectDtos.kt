package com.sekota.pmoebdesk.dashboard.data.remote.model

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
    val description: Description?,
    val percentageDone: Int? = null,
    val estimatedTime: String? = null
)

@Serializable
data class Description(
    val raw: String
)
