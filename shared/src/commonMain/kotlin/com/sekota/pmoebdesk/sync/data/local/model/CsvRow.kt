package com.sekota.pmoebdesk.sync.data.local.model

data class CsvRow(
    val projectName: String,
    val customer: String,
    val startDate: String?,
    val finishDate: String?,
    val tasks: List<String>
)
