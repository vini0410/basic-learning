package com.study.TemplateMethodApp.service

import com.study.TemplateMethodApp.domain.importer.CsvDataImporter
import com.study.TemplateMethodApp.domain.importer.ImportRecord
import com.study.TemplateMethodApp.domain.importer.ImportResult
import com.study.TemplateMethodApp.domain.importer.JsonDataImporter
import com.study.TemplateMethodApp.domain.importer.XmlDataImporter
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DataImportServiceTest {

    private lateinit var csvImporter: CsvDataImporter
    private lateinit var jsonImporter: JsonDataImporter
    private lateinit var xmlImporter: XmlDataImporter
    private lateinit var service: DataImportService

    @BeforeEach
    fun setUp() {
        csvImporter = mockk()
        jsonImporter = mockk()
        xmlImporter = mockk()
        service = DataImportService(csvImporter, jsonImporter, xmlImporter)
    }

    @Test
    fun `deve delegar importacao CSV para o csvImporter (happy path)`() {
        val expectedResult = ImportResult("CSV", 1, listOf(ImportRecord("P1", "Prod 1", 10.0)), "SUCCESS")
        every { csvImporter.processImport("content") } returns expectedResult

        val result = service.importCsv("content")

        assertEquals("CSV", result.format)
        assertEquals(1, result.totalRecords)
    }
}
