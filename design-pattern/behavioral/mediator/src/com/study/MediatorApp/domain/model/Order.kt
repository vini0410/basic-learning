package com.study.MediatorApp.domain.model

enum class OrderStatus {
    CREATED,
    CANCELLED,
    REJECTED
}

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val unitPrice: Double
) {
    val subtotal: Double
        get() = quantity * unitPrice
}

data class Order(
    val id: String,
    val customerId: String,
    val items: List<OrderItem>,
    val totalPrice: Double,
    var status: OrderStatus = OrderStatus.CREATED,
    val cancellationReason: String? = null
)
