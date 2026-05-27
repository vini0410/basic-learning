package com.notification.notificationapp.core.processor.impl

import com.notification.notificationapp.core.processor.NotificationProcessor
import com.notification.notificationapp.core.usecase.impl.WhatsAppUseCase
import org.springframework.stereotype.Component

@Component
class WhatsAppProcessor(
    private val useCase: WhatsAppUseCase, override val type: String = "WHATSAPP"
) : NotificationProcessor {

    override fun process(metadata: Map<String, Any>, message: String) {
        useCase.sendNotificationWithTwilio(metadata, message)
    }
}