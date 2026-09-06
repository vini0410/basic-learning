package com.study.TemplateMethodApp.domain.importer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class DataImporterTemplateTest {

    @Test
    fun `CsvDataImporter deve processar dados CSV e retornar lista de registros (happy path)`() {
        val csvImporter = CsvDataImporter()
        val csvContent = """
            PROD-001, Teclado Mecanico, 250.00
            PROD-002, Mouse Gamer, 120.00
        """.trimIndent()

        val result = csvImporter.processImport(csvContent)

        assertEquals("CSV", result.format)
        assertEquals("SUCCESS", result.status)
        assertEquals(2, result.totalRecords)
        assertEquals("PROD-001", result.records[0].code)
        assertEquals("Teclado Mecanico", result.records[0].name)
        assertEquals(250.0, result.records[0].price)
    }

    @Test
    fun `JsonDataImporter deve processar JSON e extrair registros (happy path)`() {
        val jsonImporter = JsonDataImporter()
        val jsonContent = """[{"code":"PROD-100","name":"Monitor 4K","price":2000.0}]"""

        val result = jsonImporter.processImport(jsonContent)

        assertEquals("JSON", result.format)
        assertEquals(1, result.totalRecords)
        assertEquals("PROD-100", result.records[0].code)
        assertEquals(2000.0, result.records[0].price)
    }

    @Test
    fun `XmlDataImporter deve processar XML e extrair registros (happy path)`() {
        val xmlImporter = XmlDataImporter()
        val xmlContent = """<items><item><code>PROD-200</code><name>Cadeira Ergonômica</name><price>1500.00</price></item></items>"""

        val result = xmlImporter.processImport(xmlContent)

        assertEquals("XML", result.format)
        assertEquals(1, result.totalRecords)
        assertEquals("PROD-200", result.records[0].code)
        assertEquals(1500.0, result.records[0].price)
    }

    @Test
    fun `deve lancar IllegalArgumentException quando o conteudo for em branco (edge case)`() {
        val csvImporter = CsvDataImporter()

        val exception = assertThrows(IllegalArgumentException::class.java) {
            csvImporter.processImport("   ")
        }
        assertEquals("Conteudo de importacao nao pode ser em branco", exception.message)
    }
}
