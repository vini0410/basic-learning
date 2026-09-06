package com.study.MediatorApp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.MediatorApp.domain.command.CancelOrderCommand
import com.study.MediatorApp.domain.command.CreateOrderCommand
import com.study.MediatorApp.domain.command.GetOrderQuery
import com.study.MediatorApp.domain.mediator.CommandMediator
import com.study.MediatorApp.domain.model.OrderStatus
import com.study.MediatorApp.dto.CancelOrderRequest
import com.study.MediatorApp.dto.CreateOrderRequest
import com.study.MediatorApp.dto.OrderItemDto
import com.study.MediatorApp.dto.OrderResponse
import com.study.MediatorApp.exception.OrderNotFoundException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OrderCommandController::class)
class OrderCommandControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mediator: CommandMediator

    @TestConfiguration
    class TestConfig {
        @Bean
        fun commandMediator(): CommandMediator = mockk()
    }

    @Test
    fun `POST api orders deve retornar 201 Created quando comando executado com sucesso (happy path)`() {
        val request = CreateOrderRequest(
            customerId = "CUST-001",
            items = listOf(OrderItemDto("PROD-001", 2, 50.0))
        )

        val expectedResponse = OrderResponse(
            id = "ORD-001",
            customerId = "CUST-001",
            items = listOf(OrderItemDto("PROD-001", 2, 50.0)),
            totalPrice = 100.0,
            status = OrderStatus.CREATED
        )

        every { mediator.send(any<CreateOrderCommand>()) } returns expectedResponse

        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("ORD-001"))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.totalPrice").value(100.0))
    }

    @Test
    fun `POST api orders id cancel deve retornar 200 OK quando cancelado (happy path)`() {
        val request = CancelOrderRequest(reason = "Mudança de ideia")

        val expectedResponse = OrderResponse(
            id = "ORD-001",
            customerId = "CUST-001",
            items = listOf(OrderItemDto("PROD-001", 2, 50.0)),
            totalPrice = 100.0,
            status = OrderStatus.CANCELLED,
            cancellationReason = "Mudança de ideia"
        )

        every { mediator.send(any<CancelOrderCommand>()) } returns expectedResponse

        mockMvc.perform(
            post("/api/orders/ORD-001/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("CANCELLED"))
            .andExpect(jsonPath("$.cancellationReason").value("Mudança de ideia"))
    }

    @Test
    fun `GET api orders id deve retornar 404 Not Found quando pedido nao existe (edge case)`() {
        every { mediator.send(any<GetOrderQuery>()) } throws OrderNotFoundException("ORD-999")

        mockMvc.perform(get("/api/orders/ORD-999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Pedido com ID 'ORD-999' nao foi encontrado"))
    }
}
