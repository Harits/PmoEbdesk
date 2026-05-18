package com.sekota.pmoebdesk.sync.data.local

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.sekota.pmoebdesk.sync.data.local.model.CsvRow
import java.io.File

class JvmCsvDataSource : CsvDataSource {
    override fun readCsv(filePath: String): List<CsvRow> {
        val file = File(filePath)
        if (!file.exists()) {
            println("      ❌ CSV file not found at $filePath")
            return emptyList()
        }

        return try {
            csvReader {
                autoRenameDuplicateHeaders = true
                skipEmptyLine = true
            }.readAllWithHeader(file).mapIndexed { index, row ->
                // Based on analysis of ISO_PMO_ET_2025_COBA_1.csv headers/row:
                // Column 1/2: Nama Projek
                // Column 3: CUSTOMER
                // Column 15: Start (but contains '5'?) -> Let's check headers vs indices
                // Column 16: Finish (contains '9-Apr-2025')
                // Column 17: Ket. (contains '30-Jun-2025')
                // Column 28: ? (contains '09')
                // Column 29: ? (contains '04')
                // Column 30: Task 1 (contains 'Pembuatan Laporan Monthly')

                val rowValues = row.values.toList()
                
                // Logging the first row to confirm mapping
                if (index == 0) {
                    println("      DEBUG: First row columns: ${rowValues.mapIndexed { i, v -> "[$i]:$v" }.joinToString(", ").take(200)}...")
                }

                CsvRow(
                    projectName = rowValues.getOrNull(1) ?: "",
                    customer = rowValues.getOrNull(3) ?: "",
                    startDate = rowValues.getOrNull(15), 
                    finishDate = rowValues.getOrNull(16),
                    statusKet = rowValues.getOrNull(17),
                    progress = rowValues.getOrNull(26)?.toIntOrNull(),
                    hours = rowValues.getOrNull(27),
                    tasks = listOfNotNull(
                        rowValues.getOrNull(28),
                        rowValues.getOrNull(29)
                    ).filter { it.isNotBlank() }
                )
            }
        } catch (e: Exception) {
            println("      ❌ Error reading CSV: ${e.message}")
            emptyList()
        }
    }
}
