package com.builder.study.models

import kotlinx.serialization.Serializable

@Serializable
enum class OutputFormat {
    PDF, EXCEL, JSON, HTML, CSV
}

@Serializable
enum class SortDirection {
    ASC, DESC
}

@Serializable
data class SortColumn(
    val field: String,
    val direction: SortDirection = SortDirection.ASC
)

@Serializable
data class Filter(
    val field: String,
    val operator: String,
    val value: String
)

@Serializable
data class ReportRequest(
    val reportName: String,
    val format: String,
    val filterBy: String? = null,
    val filterValue: String? = null,
    val sortBy: String? = null,
    val limit: Int? = null
)

@Serializable
data class ErrorResponse(
    val errorCode: String,
    val message: String
)
