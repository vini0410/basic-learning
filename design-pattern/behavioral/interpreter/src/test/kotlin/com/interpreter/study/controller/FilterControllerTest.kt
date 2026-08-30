package com.interpreter.study.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.interpreter.study.domain.Context
import com.interpreter.study.domain.FilterRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [InterpreterApp::class])
@AutoConfigureMockMvc
class FilterControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test evaluate filter endpoint with valid request`() {
        val request = mapOf(
            "filter" to "age > 18",
            "context" to mapOf("age > 18" to true)
        )

        mockMvc.perform(
            post("/api/filters/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `test evaluate filter endpoint with complex expression`() {
        val request = mapOf(
            "filter" to "age > 18 and name == John",
            "context" to mapOf("age > 18" to true, "name == John" to true)
        )

        mockMvc.perform(
            post("/api/filters/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `test evaluate filter endpoint with non-existent condition`() {
        val request = mapOf(
            "filter" to "name == John",
            "context" to mapOf("age > 18" to true)
        )

        mockMvc.perform(
            post("/api/filters/evaluate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
    }
}
