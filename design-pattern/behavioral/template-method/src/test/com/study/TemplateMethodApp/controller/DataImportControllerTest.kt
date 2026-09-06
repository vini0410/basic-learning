package com.study.TemplateMethodApp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.TemplateMethodApp.domain.importer.ImportRecord
import com.study.TemplateMethodApp.domain.importer.ImportResult
import com.study.TemplateMethodApp.dto.ImportPayloadRequest
import com.study.TemplateMethodApp.service.DataImportService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(DataImportController::class)
class DataImportControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var importService: DataImportService

    @TestConfiguration
    class TestConfig {
        @Bean
        fun dataImportService(): DataImportService = mockk()
    }

    @Test
    fun `POST api import csv deve retornar 200 OK com registros importados (happy path)`() {
        val request = ImportPayloadRequest("P-1,Item,100.0")
        val response = ImportResult("CSV", 1, listOf(ImportRecord("P-1", "Item", 100.0)), "SUCCESS")

        every { importService.importCsv(any()) } returns response

        mockMvc.perform(
            post("/api/import/csv")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.format").value("CSV"))
            .andExpect(jsonPath("$.totalRecords").value(1))
    }
}
