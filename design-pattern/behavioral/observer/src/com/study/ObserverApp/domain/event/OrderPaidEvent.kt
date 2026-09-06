package com.study.ObserverApp.domain.event

import java.time.LocalDateTime

data class OrderPaidEvent(
    val orderId: String,
    val customerId: String,
    val amount: Double,
    val paidAt: LocalDateTime = LocalDateTime.now()
)
