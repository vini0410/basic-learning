package com.abstract.study.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
enum class ChannelType {
    EMAIL, SMS, PUSH
}

@Serializable
data class NotificationRequest(
    val recipient: String,
    val content: String,
    val channelType: String
)

@Serializable
data class NotificationResult(
    val success: Boolean,
    val message: String,
    val timestamp: String
)

@Serializable
data class ErrorResponse(
    val errorCode: String,
    val message: String,
    val timestamp: String = LocalDateTime.now().toString()
)
