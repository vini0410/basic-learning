package com.notification.notificationapp.core.processor

interface NotificationProcessor {

    val type: String

    fun process(metadata: Map<String, Any>, message: String)
}