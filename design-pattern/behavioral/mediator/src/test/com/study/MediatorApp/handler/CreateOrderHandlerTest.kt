package com.study.MediatorApp.handler

import com.study.MediatorApp.domain.command.CreateOrderCommand
import com.study.MediatorApp.domain.model.Order
import com.study.MediatorApp.domain.model.OrderStatus
import com.study.MediatorApp.dto.OrderItemDto
import com.study.MediatorApp.exception.InsufficientStockException
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateOrderHandlerTest {

    private lateinit var orderRepository: OrderRepository
    private lateinit var inventoryService: InventoryService
    private lateinit var notificationService: NotificationService
    private lateinit var handler: CreateOrderHandler

    @BeforeEach
    fun setUp() {
        orderRepository = mockk()
        inventoryService = mockk()
        notificationService = mockk()
        handler = CreateOrderHandler(orderRepository, inventoryService, notificationService)
    }

    @Test
    fun `deve criar pedido com sucesso reservando estoque e notificando cliente (happy path)`() {
        val command = CreateOrderCommand(
            customerId = "CUST-100",
            items = listOf(
                OrderItemDto(productId = "PROD-001", quantity = 2, unitPrice = 50.0),
                OrderItemDto(productId = "PROD-002", quantity = 1, unitPrice = 100.0)
            )
        )

        every { inventoryService.checkAndReserveStock(any(), any()) } just runs
        val orderSlot = slot<Order>()
        every { orderRepository.save(capture(orderSlot)) } answers { orderSlot.captured }
        every { notificationService.notifyOrderCreated(any()) } just runs

        val response = handler.handle(command)

        assertNotNull(response.id)
        assertEquals("CUST-100", response.customerId)
        assertEquals(2, response.items.size)
        assertEquals(200.0, response.totalPrice)
        assertEquals(OrderStatus.CREATED, response.status)

        verify(exactly = 1) { inventoryService.checkAndReserveStock("PROD-001", 2) }
        verify(exactly = 1) { inventoryService.checkAndReserveStock("PROD-002", 1) }
        verify(exactly = 1) { orderRepository.save(any()) }
        verify(exactly = 1) { notificationService.notifyOrderCreated(any()) }
    }

    @Test
    fun `deve lancar IllegalArgumentException se o customerId estiver em branco (edge case)`() {
        val command = CreateOrderCommand(
            customerId = "   ",
            items = listOf(OrderItemDto("PROD-001", 1, 10.0))
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            handler.handle(command)
        }
        assertEquals("ID do cliente nao pode ser em branco", exception.message)
    }

    @Test
    fun `deve lancar IllegalArgumentException se a lista de itens estiver vazia (edge case)`() {
        val command = CreateOrderCommand(
            customerId = "CUST-100",
            items = emptyList()
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            handler.handle(command)
        }
        assertEquals("Pedido deve conter ao menos um item", exception.message)
    }

    @Test
    fun `deve lancar InsufficientStockException se o estoque for insuficiente (error case)`() {
        val command = CreateOrderCommand(
            customerId = "CUST-100",
            items = listOf(OrderItemDto("PROD-003", 5, 10.0))
        )

        every { inventoryService.checkAndReserveStock("PROD-003", 5) } throws InsufficientStockException("PROD-003", 5, 0)

        assertThrows(InsufficientStockException::class.java) {
            handler.handle(command)
        }

        verify(exactly = 0) { orderRepository.save(any()) }
        verify(exactly = 0) { notificationService.notifyOrderCreated(any()) }
    }
}
