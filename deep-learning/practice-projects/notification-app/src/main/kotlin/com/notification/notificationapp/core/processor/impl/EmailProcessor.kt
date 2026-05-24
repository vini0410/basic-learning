package com.notification.notificationapp.core.processor.impl

import com.notification.notificationapp.core.processor.NotificationProcessor
import com.notification.notificationapp.core.usecase.impl.EmailUseCase
import org.springframework.stereotype.Component

@Component
class EmailProcessor(
    private val useCase: EmailUseCase, override val type: String = "EMAIL"
) : NotificationProcessor {

    override fun process(metadata: Map<String, Any>, message: String) {
        useCase.sendNotification(metadata, message)
    }
}