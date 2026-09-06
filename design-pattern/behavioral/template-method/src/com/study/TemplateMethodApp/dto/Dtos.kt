package com.study.TemplateMethodApp.dto

import java.time.LocalDateTime

data class ImportPayloadRequest(
    val content: String
)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
