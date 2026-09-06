package com.study.StrategyApp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.StrategyApp.dto.CalculateDiscountRequest
import com.study.StrategyApp.dto.CalculateDiscountResponse
import com.study.StrategyApp.service.CheckoutService
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

@WebMvcTest(CheckoutController::class)
class CheckoutControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var checkoutService: CheckoutService

    @TestConfiguration
    class TestConfig {
        @Bean
        fun checkoutService(): CheckoutService = mockk()
    }

    @Test
    fun `POST api checkout calculate deve retornar 200 OK com resultado do calculo (happy path)`() {
        val request = CalculateDiscountRequest(500.0, "VIP")
        val response = CalculateDiscountResponse(500.0, 100.0, 400.0, "VIP_CUSTOMER_20_PERCENT")

        every { checkoutService.calculateCheckout(any()) } returns response

        mockMvc.perform(
            post("/api/checkout/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.originalAmount").value(500.0))
            .andExpect(jsonPath("$.discountApplied").value(100.0))
            .andExpect(jsonPath("$.finalAmount").value(400.0))
            .andExpect(jsonPath("$.strategyApplied").value("VIP_CUSTOMER_20_PERCENT"))
    }
}
