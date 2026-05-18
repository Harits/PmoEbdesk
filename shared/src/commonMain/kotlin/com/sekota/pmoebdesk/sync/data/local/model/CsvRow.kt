package com.sekota.pmoebdesk.sync.data.local.model

data class CsvRow(
    val projectName: String,
    val customer: String,
    val startDate: String?,
    val finishDate: String?,
    val progress: Int? = null,
    val hours: String? = null,
    val statusKet: String? = null,
    val tasks: List<String>
)
