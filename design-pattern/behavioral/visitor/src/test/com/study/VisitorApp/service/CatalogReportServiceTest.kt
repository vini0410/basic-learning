package com.study.VisitorApp.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CatalogReportServiceTest {

    private lateinit var service: CatalogReportService

    @BeforeEach
    fun setUp() {
        service = CatalogReportService()
    }

    @Test
    fun `deve calcular relatorio de impostos para o catalogo padrao (happy path)`() {
        val response = service.calculateTaxReport()

        assertTrue(response.totalTax > 0)
        assertTrue(response.totalPhysicalTax > 0)
        assertTrue(response.totalDigitalTax > 0)
    }

    @Test
    fun `deve gerar relatorio de resumo para o catalogo padrao (happy path)`() {
        val response = service.generateSummaryReport()

        assertEquals(2, response.physicalCount)
        assertEquals(2, response.digitalCount)
        assertEquals(2, response.categoryCount)
        assertEquals(8350.0, response.totalCatalogPrice)
    }
}
