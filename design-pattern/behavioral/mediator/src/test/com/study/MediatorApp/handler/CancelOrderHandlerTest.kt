package com.study.MediatorApp.handler

import com.study.MediatorApp.domain.command.CancelOrderCommand
import com.study.MediatorApp.domain.model.Order
import com.study.MediatorApp.domain.model.OrderItem
import com.study.MediatorApp.domain.model.OrderStatus
import com.study.MediatorApp.exception.InvalidOrderStateException
import com.study.MediatorApp.exception.OrderNotFoundException
import com.study.MediatorApp.service.InventoryService
import com.study.MediatorApp.service.NotificationService
import com.study.MediatorApp.service.OrderRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CancelOrderHandlerTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var inventoryService: InventoryService
    private lateinit var notificationService: NotificationService
    private lateinit var handler: CancelOrderHandler

    @BeforeEach
    fun setUp() {
        orderRepository = mockk()
        inventoryService = mockk()
        notificationService = mockk()
        handler = CancelOrderHandler(orderRepository, inventoryService, notificationService)
    }

    @Test
    fun `deve cancelar pedido restaurando estoque e notificando cliente (happy path)`() {
        val existingOrder = Order(
            id = "ORD-123",
            customerId = "CUST-100",
            items = listOf(OrderItem("PROD-001", 2, 50.0)),
            totalPrice = 100.0,
            status = OrderStatus.CREATED
        )

        val command = CancelOrderCommand(orderId = "ORD-123", reason = "Desistencia do cliente")

        every { orderRepository.findById("ORD-123") } returns existingOrder
        every { inventoryService.restoreStock("PROD-001", 2) } just runs
        val orderSlot = slot<Order>()
        every { orderRepository.save(capture(orderSlot)) } answers { orderSlot.captured }
        every { notificationService.notifyOrderCancelled(any(), "Desistencia do cliente") } just runs

        val response = handler.handle(command)

        assertEquals("ORD-123", response.id)
        assertEquals(OrderStatus.CANCELLED, response.status)
        assertEquals("Desistencia do cliente", response.cancellationReason)

        verify(exactly = 1) { inventoryService.restoreStock("PROD-001", 2) }
        verify(exactly = 1) { notificationService.notifyOrderCancelled(any(), "Desistencia do cliente") }
    }

    @Test
    fun `deve lancar OrderNotFoundException quando pedido nao existir (edge case)`() {
        val command = CancelOrderCommand(orderId = "ORD-999", reason = "Motivo")
        every { orderRepository.findById("ORD-999") } returns null

        assertThrows(OrderNotFoundException::class.java) {
            handler.handle(command)
        }
    }

    @Test
    fun `deve lancar InvalidOrderStateException se pedido ja estiver cancelado (edge case)`() {
        val alreadyCancelledOrder = Order(
            id = "ORD-123",
            customerId = "CUST-100",
            items = listOf(OrderItem("PROD-001", 2, 50.0)),
            totalPrice = 100.0,
            status = OrderStatus.CANCELLED
        )

        val command = CancelOrderCommand(orderId = "ORD-123", reason = "Outro motivo")
        every { orderRepository.findById("ORD-123") } returns alreadyCancelledOrder

        assertThrows(InvalidOrderStateException::class.java) {
            handler.handle(command)
        }
    }
}
