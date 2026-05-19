package com.notification.notificationapp.core.processor.impl

import com.notification.notificationapp.core.processor.NotificationProcessor
import com.notification.notificationapp.core.usecase.EmailUseCase
import org.springframework.stereotype.Component

@Component
class EmailProcessor(
    private val useCase: EmailUseCase,
) : NotificationProcessor {

    override val type = "EMAIL"

    override fun process(metadata: Map<String, Any>, message: String) {
        useCase.sendMessage(metadata, message)
    }


}