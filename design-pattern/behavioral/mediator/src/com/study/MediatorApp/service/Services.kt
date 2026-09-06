package com.study.MediatorApp.service

import com.study.MediatorApp.domain.model.Order
import com.study.MediatorApp.exception.InsufficientStockException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger {}

@Component
class OrderRepository {
    private val orders = ConcurrentHashMap<String, Order>()

    fun save(order: Order): Order {
        orders[order.id] = order
        logger.info { "Pedido salvo no repositorio: ${order.id}" }
        return order
    }

    fun findById(id: String): Order? {
        return orders[id]
    }

    fun findAll(): List<Order> {
        return orders.values.toList()
    }
}

@Component
class InventoryService {
    // Estoque inicial simulado em memoria
    private val stock = ConcurrentHashMap<String, Int>(
        mapOf(
            "PROD-001" to 50,
            "PROD-002" to 10,
            "PROD-003" to 0
        )
    )

    fun checkAndReserveStock(productId: String, quantity: Int) {
        require(quantity > 0) { "Quantidade solicitada deve ser maior que zero" }
        val currentStock = stock[productId] ?: 100 // estoque padrao para produtos nao cadastrados
        if (currentStock < quantity) {
            throw InsufficientStockException(productId, quantity, currentStock)
        }
        stock[productId] = currentStock - quantity
        logger.info { "Estoque reservado para produto $productId: $quantity unidades. Restante: ${stock[productId]}" }
    }

    fun restoreStock(productId: String, quantity: Int) {
        val currentStock = stock[productId] ?: 0
        stock[productId] = currentStock + quantity
        logger.info { "Estoque devolvido para produto $productId: $quantity unidades. Novo total: ${stock[productId]}" }
    }

    fun getStock(productId: String): Int {
        return stock[productId] ?: 0
    }
}

@Component
class NotificationService {
    fun notifyOrderCreated(order: Order) {
        logger.info { "Notificacao enviada ao cliente ${order.customerId}: Pedido ${order.id} criado com sucesso. Valor total: R$ ${order.totalPrice}" }
    }

    fun notifyOrderCancelled(order: Order, reason: String) {
        logger.info { "Notificacao enviada ao cliente ${order.customerId}: Pedido ${order.id} foi CANCELADO. Motivo: $reason" }
    }
}
