package com.builder.study.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ReportConfiguration(
    val title: String,
    val outputFormat: OutputFormat,
    val filters: List<Filter>,
    val sorting: List<SortColumn>,
    val includeHeader: Boolean,
    val includeFooter: Boolean,
    val itemsPerPage: Int,
    val createdAt: String = LocalDateTime.now().toString()
)
