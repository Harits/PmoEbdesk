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
                // Check for Template format or Source format
                val isTemplate = row.containsKey("Full_Project_Name") || row.containsKey("Report_Type")
                val isSourceFormat = row.containsKey("Nama Projek") || row.containsKey("Nama Projek_1")
                
                if (isTemplate) {
                    CsvRow(
                        id = row["ID"],
                        projectName = row["Nama Projek"] ?: "",
                        fullProjectName = row["Full_Project_Name"],
                        customer = row["CUSTOMER"] ?: "",
                        ima = row["IMA"],
                        isa = row["ISA"],
                        ias = row["IAS"],
                        bdaas = row["BDAAS"],
                        reportType = row["Report_Type"],
                        frequency = row["Frequency"],
                        owner = row["Owner"],
                        semester = row["Semester"],
                        workWeeks = row["Work_Weeks"],
                        startDate = row["Start_Date"],
                        finishDate = row["Due_Date"],
                        statusKet = row["Status_Ket"],
                        idPmo = row["ID_PMO"],
                        createdDate = row["Created_Date"],
                        updateDate = row["Update_Date"],
                        progress = row["Progress_Percent"]?.trim()?.toIntOrNull(),
                        hours = row["Estimated_Hours"],
                        tasks = listOfNotNull(
                            row["Task_1"],
                            row["Task_2"]
                        ).map { it.trim() }.filter { it.isNotBlank() },
                        docReq = row["Doc_Req"],
                        docTest = row["Doc_Test"]
                    )
                } else if (isSourceFormat) {
                    // Mapping for ISO_PMO_ET_2026.csv and ISO_PMO_ET_2025.csv
                    val name1 = row["Nama Projek"] ?: ""
                    val name2 = row["Nama Projek_1"] ?: ""
                    
                    CsvRow(
                        id = row[""], // First column is empty header
                        projectName = if (name2.isNotBlank()) name2 else name1,
                        fullProjectName = if (name2.isNotBlank()) name2 else name1,
                        customer = row["CUSTOMER"] ?: "",
                        ima = row["IMA"],
                        isa = row["ISA"],
                        ias = row["IAS"],
                        bdaas = row["BDAAS"],
                        reportType = row["Report"],
                        frequency = "Monthly", // Standard frequency for these files
                        owner = row["SALES"],
                        semester = row["Semester"],
                        workWeeks = row["Waktu kerja sama"],
                        startDate = row["Start"],
                        finishDate = row["Finish"],
                        statusKet = row["Ket."],
                        idPmo = row["ID PMO"],
                        createdDate = row["Tgl Dibuat"],
                        updateDate = row["Tgl Last Update"],
                        tasks = listOfNotNull(
                            row["Task 1"],
                            row["Task 2"]
                        ).map { it.trim() }.filter { it.isNotBlank() },
                        docReq = row["capture Doc Permintaan"],
                        docTest = row["Doc Pengujian"]
                    )
                } else {
                    // Simple format fallback
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
