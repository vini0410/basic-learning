package com.study.VisitorApp.controller

import com.study.VisitorApp.dto.SummaryReportResponse
import com.study.VisitorApp.dto.TaxReportResponse
import com.study.VisitorApp.service.CatalogReportService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CatalogReportController::class)
class CatalogReportControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var reportService: CatalogReportService

    @TestConfiguration
    class TestConfig {
        @Bean
        fun catalogReportService(): CatalogReportService = mockk()
    }

    @Test
    fun `GET api catalog reports tax deve retornar 200 OK (happy path)`() {
        val response = TaxReportResponse(100.0, 80.0, 20.0)
        every { reportService.calculateTaxReport() } returns response

        mockMvc.perform(get("/api/catalog/reports/tax"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalTax").value(100.0))
            .andExpect(jsonPath("$.totalPhysicalTax").value(80.0))
            .andExpect(jsonPath("$.totalDigitalTax").value(20.0))
    }

    @Test
    fun `GET api catalog reports summary deve retornar 200 OK (happy path)`() {
        val response = SummaryReportResponse(2, 2, 2, 8000.0)
        every { reportService.generateSummaryReport() } returns response

        mockMvc.perform(get("/api/catalog/reports/summary"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.physicalCount").value(2))
            .andExpect(jsonPath("$.digitalCount").value(2))
            .andExpect(jsonPath("$.totalCatalogPrice").value(8000.0))
    }
}
