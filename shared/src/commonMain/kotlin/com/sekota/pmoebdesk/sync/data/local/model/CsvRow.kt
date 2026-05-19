package com.sekota.pmoebdesk.sync.data.local.model

data class CsvRow(
    val id: String? = null,
    val projectName: String,
    val fullProjectName: String? = null,
    val customer: String,
    val ima: String? = null,
    val isa: String? = null,
    val ias: String? = null,
    val bdaas: String? = null,
    val reportType: String? = null,
    val frequency: String? = null,
    val owner: String? = null,
    val semester: String? = null,
    val workWeeks: String? = null,
    val startDate: String? = null,
    val finishDate: String? = null,
    val statusKet: String? = null,
    val idPmo: String? = null,
    val createdDate: String? = null,
    val updateDate: String? = null,
    val progress: Int? = null,
    val hours: String? = null,
    val tasks: List<String>,
    val docReq: String? = null,
    val docTest: String? = null
)
