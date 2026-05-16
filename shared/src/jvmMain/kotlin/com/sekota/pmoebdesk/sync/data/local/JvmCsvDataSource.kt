package com.sekota.pmoebdesk.sync.data.local

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.sekota.pmoebdesk.sync.data.local.model.CsvRow
import java.io.File

class JvmCsvDataSource : CsvDataSource {
    override fun readCsv(filePath: String): List<CsvRow> {
        val file = File(filePath)
        if (!file.exists()) return emptyList()

        return csvReader {
            autoRenameDuplicateHeaders = true
        }.readAllWithHeader(file).map { row ->
            CsvRow(
                projectName = row["Nama Projek"] ?: "",
                customer = row["CUSTOMER"] ?: "",
                startDate = row["Start"],
                finishDate = row["Finish"],
                tasks = listOfNotNull(row["Task 1"], row["Task 2"]).filter { it.isNotBlank() }
            )
        }
    }
}
