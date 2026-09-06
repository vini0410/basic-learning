package com.study.MediatorApp.handler

import com.study.MediatorApp.domain.command.CancelOrderCommand
import com.study.MediatorApp.domain.command.CreateOrderCommand
import com.study.MediatorApp.domain.command.GetOrderQuery
import com.study.MediatorApp.domain.mediator.CommandHandler
import com.study.MediatorApp.domain.model.Order
import com.study.MediatorApp.domain.model.OrderItem
import com.study.MediatorApp.domain.model.OrderStatus
import com.study.MediatorApp.dto.OrderResponse
import com.study.MediatorApp.exception.InvalidOrderStateException
import com.study.MediatorApp.exception.OrderNotFoundException
import com.study.MediatorApp.service.InventoryService
import com.study.MediatorApp.service.NotificationService
import com.study.MediatorApp.service.OrderRepository
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class CreateOrderHandler(
    private val orderRepository: OrderRepository,
    private val inventoryService: InventoryService,
    private val notificationService: NotificationService
) : CommandHandler<CreateOrderCommand, OrderResponse> {

    override fun handle(command: CreateOrderCommand): OrderResponse {
        logger.info { "Processando CreateOrderCommand para o cliente ${command.customerId}" }
        
        require(command.customerId.isNotBlank()) { "ID do cliente nao pode ser em branco" }
        require(command.items.isNotEmpty()) { "Pedido deve conter ao menos um item" }

        // 1. Validar e reservar estoque para cada item
        val orderItems = command.items.map { itemDto ->
            require(itemDto.quantity > 0) { "Quantidade do produto ${itemDto.productId} deve ser maior que zero" }
            require(itemDto.unitPrice >= 0) { "Preco unitario do produto ${itemDto.productId} nao pode ser negativo" }
            
            inventoryService.checkAndReserveStock(itemDto.productId, itemDto.quantity)
            OrderItem(
                productId = itemDto.productId,
                quantity = itemDto.quantity,
                unitPrice = itemDto.unitPrice
            )
        }

        // 2. Calcular total
        val total = orderItems.sumOf { it.subtotal }

        // 3. Criar entidade e salvar no repositorio
        val order = Order(
            id = UUID.randomUUID().toString(),
            customerId = command.customerId,
            items = orderItems,
            totalPrice = total,
            status = OrderStatus.CREATED
        )
        val savedOrder = orderRepository.save(order)

        // 4. Notificar cliente
        notificationService.notifyOrderCreated(savedOrder)

        return OrderResponse.fromDomain(savedOrder)
    }
}

@Component
class CancelOrderHandler(
    private val orderRepository: OrderRepository,
    private val inventoryService: InventoryService,
    private val notificationService: NotificationService
) : CommandHandler<CancelOrderCommand, OrderResponse> {

    override fun handle(command: CancelOrderCommand): OrderResponse {
        logger.info { "Processando CancelOrderCommand para o pedido ${command.orderId}" }
        
        require(command.reason.isNotBlank()) { "Motivo do cancelamento deve ser informado" }

        // 1. Buscar pedido
        val order = orderRepository.findById(command.orderId)
            ?: throw OrderNotFoundException(command.orderId)

        // 2. Validar estado do pedido
        if (order.status == OrderStatus.CANCELLED) {
            throw InvalidOrderStateException("Pedido ${command.orderId} ja esta cancelado")
        }

        // 3. Devolver estoque
        order.items.forEach { item ->
            inventoryService.restoreStock(item.productId, item.quantity)
        }

        // 4. Atualizar pedido
        val updatedOrder = order.copy(
            status = OrderStatus.CANCELLED,
            cancellationReason = command.reason
        )
        orderRepository.save(updatedOrder)

        // 5. Notificar cliente
        notificationService.notifyOrderCancelled(updatedOrder, command.reason)

        return OrderResponse.fromDomain(updatedOrder)
    }
}

@Component
class GetOrderHandler(
    private val orderRepository: OrderRepository
) : CommandHandler<GetOrderQuery, OrderResponse> {

    override fun handle(command: GetOrderQuery): OrderResponse {
        val order = orderRepository.findById(command.orderId)
            ?: throw OrderNotFoundException(command.orderId)
        return OrderResponse.fromDomain(order)
    }
}
