package com.study.StateApp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.StateApp.dto.CreateOrderRequest
import com.study.StateApp.dto.OrderResponse
import com.study.StateApp.service.OrderLifecycleService
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

@WebMvcTest(OrderStateController::class)
class OrderStateControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var orderService: OrderLifecycleService

    @TestConfiguration
    class TestConfig {
        @Bean
        fun orderLifecycleService(): OrderLifecycleService = mockk()
    }

    @Test
    fun `POST api orders deve criar pedido e retornar 201 Created (happy path)`() {
        val request = CreateOrderRequest("CUST-1", 100.0)
        val response = OrderResponse("ORD-1", "CUST-1", 100.0, "PENDING_PAYMENT")

        every { orderService.createOrder(any()) } returns response

        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("PENDING_PAYMENT"))
    }

    @Test
    fun `POST api orders id pay deve pagar pedido e retornar 200 OK (happy path)`() {
        val response = OrderResponse("ORD-1", "CUST-1", 100.0, "PAID")

        every { orderService.payOrder("ORD-1") } returns response

        mockMvc.perform(post("/api/orders/ORD-1/pay"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("PAID"))
    }

    @Test
    fun `POST api orders id ship deve retornar 400 Bad Request se a transacao de estado for invalida (edge case)`() {
        every { orderService.shipOrder("ORD-1") } throws IllegalStateException("Nao e possivel enviar um pedido que aguarda pagamento")

        mockMvc.perform(post("/api/orders/ORD-1/ship"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Bad Request / Invalid State Transition"))
            .andExpect(jsonPath("$.message").value("Nao e possivel enviar um pedido que aguarda pagamento"))
    }
}
