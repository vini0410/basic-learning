package com.notification.notificationapp.core.processor.impl

import com.notification.notificationapp.core.processor.NotificationProcessor
import com.notification.notificationapp.core.usecase.impl.SmsUseCase
import org.springframework.stereotype.Component

@Component
class SmsProcessor(
    private val useCase: SmsUseCase, override val type: String = "SMS"
) : NotificationProcessor {

    override fun process(metadata: Map<String, Any>, message: String) {
        useCase.sendNotification(metadata, message)
    }
}