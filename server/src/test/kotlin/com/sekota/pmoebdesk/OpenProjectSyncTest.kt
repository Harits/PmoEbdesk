package com.sekota.pmoebdesk

import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class OpenProjectSyncTest {

    @Test
    fun testSlugify() {
        assertEquals("my-new-project", slugify("My New Project"))
        assertEquals("project-123-abc", slugify("Project 123 @ ABC!!!"))
        assertEquals("et-006-09-04-25-pt-karlin-mastrindo", slugify("ET-006-09-04-25-PT Karlin Mastrindo"))
    }

    @Test
    fun testFormatDate() {
        assertEquals("2025-04-09", formatDate("9-Apr-2025"))
        assertEquals("2025-07-01", formatDate("1-Jul-2025"))
        assertEquals("2025-10-30", formatDate("30-Oct-2025"))
        assertEquals(null, formatDate("invalid-date"))
        assertEquals(null, formatDate(""))
    }

    @Test
    fun testCsvParsing() {
        val csvContent = """,Nama Projek,Nama Projek,CUSTOMER,,IMA,ISA,IAS,BDAAS,Report,AI,HARDWARE,SALES,Semester,Waktu kerja sama,Start,Finish,Ket.,ID PMO,Tgl Dibuat,Tgl Last Update,,,,,,,,Task 1,Task 2,capture Doc Permintaan,Doc Pengujian
6,ET-006-09-04-25-PT Karlin Mastrindo,ET-006-09-04-25-PT Karlin Mastrindo,PT Karlin Mastrindo,,,,,Progress Laporan,Monthly,,,Gavin,S1,5,9-Apr-2025,30-Jun-2025,NGO,,11-Apr-2025,,04,0,,,,09,04,Pembuatan Laporan Monthly,,Belum,Belum"""
        
        val reader = com.github.doyaaaaaken.kotlincsv.dsl.csvReader {
            autoRenameDuplicateHeaders = true
        }
        val rows = reader.readAllWithHeader(csvContent)
        assertEquals(1, rows.size)
        val row = rows[0]
        assertEquals("ET-006-09-04-25-PT Karlin Mastrindo", row["Nama Projek"])
        assertEquals("PT Karlin Mastrindo", row["CUSTOMER"])
        assertEquals("9-Apr-2025", row["Start"])
        assertEquals("30-Jun-2025", row["Finish"])
        assertEquals("Pembuatan Laporan Monthly", row["Task 1"])
    }

    @Test
    fun testExpandTaskMonthly() {
        val subject = "Pembuatan Laporan Monthly"
        val startDate = "2025-04-09"
        val finishDate = "2025-06-30"
        val referenceDate = LocalDate.parse("2025-05-15")
        
        val expanded = expandTask(subject, startDate, finishDate, referenceDate)
        
        // April, May, June -> 3 tasks
        assertEquals(3, expanded.size)
        assertEquals("Pembuatan Laporan Monthly - April 2025", expanded[0].subject)
        assertEquals("2025-04-09", expanded[0].startDate)
        assertEquals("2025-04-30", expanded[0].dueDate)
        assertEquals("Closed", expanded[0].status) // April is before May
        
        assertEquals("Pembuatan Laporan Monthly - May 2025", expanded[1].subject)
        assertEquals("2025-05-01", expanded[1].startDate)
        assertEquals("2025-05-31", expanded[1].dueDate)
        assertEquals("New", expanded[1].status) // May is current month
        
        assertEquals("Pembuatan Laporan Monthly - June 2025", expanded[2].subject)
        assertEquals("2025-06-01", expanded[2].startDate)
        assertEquals("2025-06-30", expanded[2].dueDate)
        assertEquals("New", expanded[2].status) // June is future month
    }

    @Test
    fun testExpandTaskRegular() {
        val subject = "Regular Task"
        val startDate = "2025-04-09"
        val finishDate = "2025-06-30"
        val referenceDate = LocalDate.parse("2025-05-15")
        
        val expanded = expandTask(subject, startDate, finishDate, referenceDate)
        assertEquals(1, expanded.size)
        assertEquals("Regular Task", expanded[0].subject)
        assertEquals("New", expanded[0].status) // Task ends in June, which is future
    }

    @Test
    fun testExpandTaskPastRegular() {
        val subject = "Past Task"
        val startDate = "2025-01-01"
        val finishDate = "2025-01-31"
        val referenceDate = LocalDate.parse("2025-05-15")
        
        val expanded = expandTask(subject, startDate, finishDate, referenceDate)
        assertEquals(1, expanded.size)
        assertEquals("Closed", expanded[0].status) // Task ends in Jan, which is past
    }
}
