package com.study.StateApp.service

import com.study.StateApp.dto.CreateOrderRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderLifecycleServiceTest {

    private lateinit var service: OrderLifecycleService

    @BeforeEach
    fun setUp() {
        service = OrderLifecycleService()
    }

    @Test
    fun `deve criar pedido no estado PENDING_PAYMENT (happy path)`() {
        val response = service.createOrder(CreateOrderRequest("CUST-1", 150.0))

        assertEquals("CUST-1", response.customerId)
        assertEquals("PENDING_PAYMENT", response.status)
        assertEquals(150.0, response.totalAmount)
    }

    @Test
    fun `deve transicionar pedido para PAID e SHIPPED via service (happy path)`() {
        val created = service.createOrder(CreateOrderRequest("CUST-1", 150.0))

        val paid = service.payOrder(created.id)
        assertEquals("PAID", paid.status)

        val shipped = service.shipOrder(created.id)
        assertEquals("SHIPPED", shipped.status)
    }

    @Test
    fun `deve lancar NoSuchElementException ao tentar transicionar pedido inexistente (edge case)`() {
        assertThrows(NoSuchElementException::class.java) {
            service.payOrder("ORD-999")
        }
    }
}
