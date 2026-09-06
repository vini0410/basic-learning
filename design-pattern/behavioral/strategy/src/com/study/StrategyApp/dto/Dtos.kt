package com.study.StrategyApp.dto

import java.time.LocalDateTime

data class CalculateDiscountRequest(
    val amount: Double,
    val couponOrType: String? = null
)

data class CalculateDiscountResponse(
    val originalAmount: Double,
    val discountApplied: Double,
    val finalAmount: Double,
    val strategyApplied: String
)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
