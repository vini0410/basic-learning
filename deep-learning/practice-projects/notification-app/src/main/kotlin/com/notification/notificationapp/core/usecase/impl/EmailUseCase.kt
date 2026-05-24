package com.notification.notificationapp.core.usecase.impl

import com.notification.notificationapp.core.usecase.NotificationUseCase
import com.notification.notificationapp.integration.output.email.SendEmail
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EmailUseCase(
    private val integration: SendEmail
) : NotificationUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun sendNotification(metadata: Map<String, Any>, message: String) {
        logger.info("useCase: received: '{}' -- '{}'", metadata, message)

        val target = metadata["target"] ?: throw RuntimeException("target Email missing")
        val subject = metadata["subject"] ?: throw RuntimeException("subject Email missing")

        try {
            integration.sendEmail(target.toString(), subject.toString(), message)
        } catch (e: Exception) {
            logger.error("Error while sending email", e)
            throw RuntimeException("Error sending email")
        }
    }
}