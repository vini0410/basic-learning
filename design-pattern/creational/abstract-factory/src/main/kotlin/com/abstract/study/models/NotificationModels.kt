package com.abstract.study.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
enum class ChannelType {
    EMAIL, SMS, PUSH
}

@Serializable
data class NotificationRequest(
    val recipient: String,
    val content: String,
    val channelType: ChannelType
)

@Serializable
data class NotificationResult(
    val success: Boolean,
    val message: String,
    val timestamp: String // Simplified for serialization without custom serializers
)
