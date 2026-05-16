package com.sekota.pmoebdesk.sync.data.local

import com.sekota.pmoebdesk.sync.data.local.model.CsvRow

interface CsvDataSource {
    fun readCsv(filePath: String): List<CsvRow>
}
