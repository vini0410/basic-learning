package com.study.StateApp.dto

import com.study.StateApp.domain.context.OrderContext
import com.study.StateApp.domain.state.CancelledState
import java.time.LocalDateTime

data class CreateOrderRequest(
    val customerId: String,
    val totalAmount: Double
)

data class CancelOrderRequest(
    val reason: String
)

data class OrderResponse(
    val id: String,
    val customerId: String,
    val totalAmount: Double,
    val status: String,
    val cancellationReason: String? = null
) {
    companion object {
        fun fromDomain(context: OrderContext): OrderResponse {
            val state = context.currentState
            val reason = if (state is CancelledState) state.reason else null
            return OrderResponse(
                id = context.id,
                customerId = context.customerId,
                totalAmount = context.totalAmount,
                status = state.getName(),
                cancellationReason = reason
            )
        }
    }
}

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
