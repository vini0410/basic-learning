package com.study.MediatorApp.dto

import com.study.MediatorApp.domain.model.Order
import com.study.MediatorApp.domain.model.OrderStatus
import java.time.LocalDateTime

data class OrderItemDto(
    val productId: String,
    val quantity: Int,
    val unitPrice: Double
)

data class CreateOrderRequest(
    val customerId: String,
    val items: List<OrderItemDto>
)

data class OrderResponse(
    val id: String,
    val customerId: String,
    val items: List<OrderItemDto>,
    val totalPrice: Double,
    val status: OrderStatus,
    val cancellationReason: String? = null
) {
    companion object {
        fun fromDomain(order: Order): OrderResponse {
            return OrderResponse(
                id = order.id,
                customerId = order.customerId,
                items = order.items.map { OrderItemDto(it.productId, it.quantity, it.unitPrice) },
                totalPrice = order.totalPrice,
                status = order.status,
                cancellationReason = order.cancellationReason
            )
        }
    }
}

data class CancelOrderRequest(
    val reason: String
)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
