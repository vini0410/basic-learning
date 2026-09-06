package com.study.MediatorApp.domain.command

import com.study.MediatorApp.dto.OrderItemDto
import com.study.MediatorApp.dto.OrderResponse

data class CreateOrderCommand(
    val customerId: String,
    val items: List<OrderItemDto>
) : Command<OrderResponse>

data class CancelOrderCommand(
    val orderId: String,
    val reason: String
) : Command<OrderResponse>

data class GetOrderQuery(
    val orderId: String
) : Command<OrderResponse>
