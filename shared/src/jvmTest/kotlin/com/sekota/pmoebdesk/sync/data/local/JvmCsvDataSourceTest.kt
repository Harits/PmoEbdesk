package com.sekota.pmoebdesk.sync.data.local

import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmCsvDataSourceTest {

    private lateinit var testFile: File

    @BeforeTest
    fun setup() {
        testFile = File.createTempFile("test_projects", ".csv")
        testFile.writeText(
            """Nama Projek,CUSTOMER,Start,Finish,Progress,Hours,Task 1,Task 2
Project A,Client X,01-Jan-2025,31-Mar-2025,50%,8,Implementation,Testing
"""
        )
    }

    @AfterTest
    fun tearDown() {
        testFile.delete()
    }

    @Test
    fun testReadCsv() {
        val dataSource = JvmCsvDataSource()
        val rows = dataSource.readCsv(testFile.absolutePath)

        assertEquals(1, rows.size)
        val row = rows[0]
        assertEquals("Project A", row.projectName)
        assertEquals("Client X", row.customer)
        assertEquals("01-Jan-2025", row.startDate)
        assertEquals("31-Mar-2025", row.finishDate)
        assertEquals(50, row.progress)
        assertEquals("8", row.hours)
        assertEquals(2, row.tasks.size)
        assertEquals("Implementation", row.tasks[0])
        assertEquals("Testing", row.tasks[1])
    }
}
