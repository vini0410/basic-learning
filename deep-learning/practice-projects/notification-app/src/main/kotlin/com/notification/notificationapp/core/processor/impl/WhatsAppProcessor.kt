package com.notification.notificationapp.core.processor.impl

import com.notification.notificationapp.core.processor.NotificationProcessor
import com.notification.notificationapp.core.usecase.impl.WhatsAppUseCase
import org.springframework.stereotype.Component

@Component
class WhatsAppProcessor(
    private val useCase: WhatsAppUseCase,
) : NotificationProcessor {

    override val type = "WHATSAPP"

    override fun process(metadata: Map<String, Any>, message: String) {
        useCase.sendNotification(metadata, message)
    }
}