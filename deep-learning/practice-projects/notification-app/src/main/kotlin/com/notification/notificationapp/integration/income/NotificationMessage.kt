package com.notification.notificationapp.integration.income

data class NotificationMessage(
    val type: String,
    val metadata: Map<String, Any>,
    val message: String
    )