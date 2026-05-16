package com.sekota.pmoebdesk.projects.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectsResponse(
    val total: Int,
    val count: Int,
    val _embedded: EmbeddedProjects
)

@Serializable
data class EmbeddedProjects(
    val elements: List<ProjectElement>
)

@Serializable
data class ProjectElement(
    val id: Int,
    val identifier: String,
    val name: String,
    val status: String? = null,
    val statusExplanation: Description? = null,
    val parent: ParentLink? = null
)

@Serializable
data class Description(
    val format: String = "markdown",
    val raw: String
)

@Serializable
data class ParentLink(
    val id: Int? = null,
    val name: String? = null,
    val _links: Links? = null
)

@Serializable
data class Links(
    val self: Link? = null
)

@Serializable
data class Link(
    val href: String? = null,
    val title: String? = null
)
