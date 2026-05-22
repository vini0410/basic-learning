package com.notification.notificationapp.core.usecase

interface NotificationUseCase {

    fun sendNotification(metadata: Map<String, Any>, message: String)
}