package com.factory.study.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val method: PaymentMethod,
    val amount: Double
)
