package com.sekota.pmoebdesk.sync.data.local

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmCsvDataSourceTest {

    @Test
    fun testReadCsvSourceFormat2026() {
        val headers = ",Nama Projek,Nama Projek,CUSTOMER,,IMA,ISA,IAS,BDAAS,Report,AI,HARDWARE,SALES,Semester,Waktu kerja sama,Start,Finish,Ket.,ID PMO,Tgl Dibuat,Tgl Last Update,B1,B2,B3,B4,B5,B6,B7,Task 1,Task 2,capture Doc Permintaan,Doc Pengujian"
        val values = "1,ET-001-01-01-26-CTFK Medmon,ET-001-01-01-26-CTFK Medmon,CTFK Medmon,Desc,2 user,ISA,IAS,BDAAS,Monthly,AI,HW,Gavin,S1,12,1-Jan-2026,31-Dec-2026,NGO,PMO-001,3-Jan-2026,4-Jan-2026,,,,,,,,Task A,Task B,Belum,Belum"
        
        val testFileSource = File.createTempFile("test_source_2026", ".csv")
        testFileSource.writeText("$headers\n$values")
        
        val dataSource = JvmCsvDataSource()
        val rows = dataSource.readCsv(testFileSource.absolutePath)

        assertEquals(1, rows.size)
        val row = rows[0]
        assertEquals("ET-001-01-01-26-CTFK Medmon", row.projectName)
        assertEquals("CTFK Medmon", row.customer)
        assertEquals("1-Jan-2026", row.startDate)
        assertEquals("31-Dec-2026", row.finishDate)
        assertEquals(2, row.tasks.size)
        assertEquals("Task A", row.tasks[0])
        assertEquals("Task B", row.tasks[1])
        assertEquals("Belum", row.docReq)
        assertEquals("Belum", row.docTest)
        
        testFileSource.delete()
    }

    @Test
    fun testReadCsvTemplateFormat() {
        val headers = listOf(
            "ID", "Nama Projek", "Full_Project_Name", "CUSTOMER", "Blank1", "IMA", "ISA", "IAS", "BDAAS", 
            "Report_Type", "Frequency", "Blank2", "Owner", "Semester", "Work_Weeks", "Start_Date", "Due_Date", 
            "Status_Ket", "ID_PMO", "Created_Date", "Update_Date", "Blank3", "Blank4", "Blank5", "Blank6", "Blank7", 
            "Progress_Percent", "Estimated_Hours", "Task_1", "Task_2", "Doc_Req", "Doc_Test"
        )
        val values = mutableListOf<String>().apply {
            repeat(headers.size) { add("") }
        }
        
        values[headers.indexOf("ID")] = "1"
        values[headers.indexOf("Nama Projek")] = "Project A"
        values[headers.indexOf("Full_Project_Name")] = "Full Project A"
        values[headers.indexOf("CUSTOMER")] = "Client X"
        values[headers.indexOf("Frequency")] = "Monthly"
        values[headers.indexOf("Owner")] = "Gavin"
        values[headers.indexOf("Semester")] = "S1"
        values[headers.indexOf("Work_Weeks")] = "12"
        values[headers.indexOf("Start_Date")] = "01-Jan-2026"
        values[headers.indexOf("Due_Date")] = "31-Dec-2026"
        values[headers.indexOf("Status_Ket")] = "NGO"
        values[headers.indexOf("ID_PMO")] = "PMO-001"
        values[headers.indexOf("Progress_Percent")] = "50"
        values[headers.indexOf("Estimated_Hours")] = "8"
        values[headers.indexOf("Task_1")] = "Implementation"
        values[headers.indexOf("Task_2")] = "Testing"
        values[headers.indexOf("Doc_Req")] = "Done"
        values[headers.indexOf("Doc_Test")] = "Done"

        val testFileTemplate = File.createTempFile("test_template", ".csv")
        testFileTemplate.writeText(headers.joinToString(",") + "\n" + values.joinToString(","))

        val dataSource = JvmCsvDataSource()
        val rows = dataSource.readCsv(testFileTemplate.absolutePath)

        assertEquals(1, rows.size)
        val row = rows[0]
        assertEquals("Project A", row.projectName)
        assertEquals("Full Project A", row.fullProjectName)
        assertEquals("Client X", row.customer)
        assertEquals("01-Jan-2026", row.startDate)
        assertEquals("31-Dec-2026", row.finishDate)
        assertEquals(50, row.progress)
        assertEquals("8", row.hours)
        assertEquals(2, row.tasks.size)
        assertEquals("Implementation", row.tasks[0])
        assertEquals("Testing", row.tasks[1])
        assertEquals("Done", row.docReq)
        assertEquals("Done", row.docTest)
        
        testFileTemplate.delete()
    }
}
