package com.notification.notificationapp.core.usecase.impl

import com.notification.notificationapp.core.usecase.NotificationUseCase
import com.notification.notificationapp.integration.output.sms.SendSms
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SmsUseCase(
    private val integration: SendSms
): NotificationUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun sendNotification(metadata: Map<String, Any>, message: String) {
        logger.info("useCase: received: '{}' -- '{}'", metadata, message)

        val target = metadata["target"] ?: throw RuntimeException("target SMS missing")

        try {
            integration.sendSMS(target.toString(), message)
    } catch (e: Exception) {
           logger.error("Error while sending sms", e)
            throw RuntimeException("Error sending sms")
        }
    }
}