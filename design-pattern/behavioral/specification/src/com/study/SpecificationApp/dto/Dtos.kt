package com.study.SpecificationApp.dto

import java.time.LocalDateTime

data class EligibilityCheckResponse(
    val customerId: String,
    val customerName: String,
    val ruleName: String,
    val isEligible: Boolean
)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
