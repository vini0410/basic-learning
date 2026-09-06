package com.study.StateApp.service

import com.study.StateApp.domain.context.OrderContext
import com.study.StateApp.dto.CreateOrderRequest
import com.study.StateApp.dto.OrderResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger {}

@Service
class OrderLifecycleService {

    private val orders = ConcurrentHashMap<String, OrderContext>()

    fun createOrder(request: CreateOrderRequest): OrderResponse {
        require(request.customerId.isNotBlank()) { "ID do cliente nao pode ser em branco" }
        require(request.totalAmount > 0) { "Valor total do pedido deve ser maior que zero" }

        val order = OrderContext(
            id = UUID.randomUUID().toString(),
            customerId = request.customerId,
            totalAmount = request.totalAmount
        )
        orders[order.id] = order
        logger.info { "Pedido '${order.id}' criado no estado inicial '${order.currentState.getName()}'" }
        return OrderResponse.fromDomain(order)
    }

    fun payOrder(orderId: String): OrderResponse {
        val order = getOrderContext(orderId)
        order.pay()
        logger.info { "Pedido '${orderId}' transicionou para o estado '${order.currentState.getName()}'" }
        return OrderResponse.fromDomain(order)
    }

    fun shipOrder(orderId: String): OrderResponse {
        val order = getOrderContext(orderId)
        order.ship()
        logger.info { "Pedido '${orderId}' transicionou para o estado '${order.currentState.getName()}'" }
        return OrderResponse.fromDomain(order)
    }

    fun deliverOrder(orderId: String): OrderResponse {
        val order = getOrderContext(orderId)
        order.deliver()
        logger.info { "Pedido '${orderId}' transicionou para o estado '${order.currentState.getName()}'" }
        return OrderResponse.fromDomain(order)
    }

    fun cancelOrder(orderId: String, reason: String): OrderResponse {
        require(reason.isNotBlank()) { "Motivo do cancelamento nao pode ser em branco" }
        val order = getOrderContext(orderId)
        order.cancel(reason)
        logger.info { "Pedido '${orderId}' transicionou para o estado '${order.currentState.getName()}'. Motivo: $reason" }
        return OrderResponse.fromDomain(order)
    }

    fun getOrder(orderId: String): OrderResponse {
        val order = getOrderContext(orderId)
        return OrderResponse.fromDomain(order)
    }

    private fun getOrderContext(orderId: String): OrderContext {
        return orders[orderId]
            ?: throw NoSuchElementException("Pedido com ID '$orderId' nao foi encontrado")
    }
}
