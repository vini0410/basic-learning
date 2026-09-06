package com.study.ObserverApp.dto

import java.time.LocalDateTime

data class ConfirmPaymentRequest(
    val orderId: String,
    val customerId: String,
    val amount: Double
)

data class PaymentResponse(
    val orderId: String,
    val status: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
