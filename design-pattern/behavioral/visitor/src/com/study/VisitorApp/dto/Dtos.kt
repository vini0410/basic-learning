package com.study.VisitorApp.dto

import java.time.LocalDateTime

data class TaxReportResponse(
    val totalTax: Double,
    val totalPhysicalTax: Double,
    val totalDigitalTax: Double
)

data class SummaryReportResponse(
    val physicalCount: Int,
    val digitalCount: Int,
    val categoryCount: Int,
    val totalCatalogPrice: Double
)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
