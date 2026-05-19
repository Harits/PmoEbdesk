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
            }.readAllWithHeader(file).map { row ->
                // Use headers for safer mapping when available
                val isComplex = row.containsKey("Start_Date") || row.containsKey("Due_Date")
                
                if (isComplex) {
                    CsvRow(
                        projectName = row["Nama Projek"] ?: row["Full_Project_Name"] ?: "",
                        customer = row["CUSTOMER"] ?: "",
                        startDate = row["Start_Date"],
                        finishDate = row["Due_Date"],
                        statusKet = row["Status_Ket"],
                        progress = row["Progress_Percent"]?.trim()?.toIntOrNull(),
                        hours = row["Estimated_Hours"],
                        tasks = listOfNotNull(
                            row["Task_1"],
                            row["Task_2"]
                        ).map { it.trim() }.filter { it.isNotBlank() }
                    )
                } else {
                    // Simple format fallback (like in tests)
                    CsvRow(
                        projectName = row["Nama Projek"] ?: row["Project Name"] ?: "",
                        customer = row["CUSTOMER"] ?: row["Customer"] ?: "",
                        startDate = row["Start"] ?: row["Start Date"],
                        finishDate = row["Finish"] ?: row["Finish Date"],
                        statusKet = row["Ket."] ?: row["Status"],
                        progress = (row["Progress"] ?: row["Progress %"])?.replace("%", "")?.trim()?.toIntOrNull(),
                        hours = row["Hours"] ?: row["Estimated Hours"],
                        tasks = row.filterKeys { it.startsWith("Task") }.values.map { it.trim() }.filter { it.isNotBlank() }
                    )
                }
            }
        } catch (e: Exception) {
            println("      ❌ Error reading CSV: ${e.message}")
            emptyList()
        }
    }
}
