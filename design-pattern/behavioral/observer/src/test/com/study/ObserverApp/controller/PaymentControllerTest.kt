package com.study.ObserverApp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.ObserverApp.dto.ConfirmPaymentRequest
import com.study.ObserverApp.dto.PaymentResponse
import com.study.ObserverApp.service.PaymentService
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

@WebMvcTest(PaymentController::class)
class PaymentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var paymentService: PaymentService

    @TestConfiguration
    class TestConfig {
        @Bean
        fun paymentService(): PaymentService = mockk()
    }

    @Test
    fun `POST api payments confirm deve retornar 200 OK (happy path)`() {
        val request = ConfirmPaymentRequest("ORD-100", "CUST-100", 200.0)
        val response = PaymentResponse("ORD-100", "PAID", "Sucesso")

        every { paymentService.processPayment(any()) } returns response

        mockMvc.perform(
            post("/api/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.orderId").value("ORD-100"))
            .andExpect(jsonPath("$.status").value("PAID"))
    }
}
