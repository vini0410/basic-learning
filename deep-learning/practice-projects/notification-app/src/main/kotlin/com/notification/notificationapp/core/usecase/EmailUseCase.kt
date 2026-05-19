package com.notification.notificationapp.core.usecase

import com.notification.notificationapp.integration.output.email.SendEmail
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EmailUseCase(
    private val integration: SendEmail
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendMessage(metadata: Map<String, Any>, message: String) {
        logger.info("useCase: received: '{}' -- '{}'", metadata, message)

        val target = metadata["target"] ?: throw RuntimeException("target Email missing")
        val subject = metadata["subject"] ?: throw RuntimeException("subject Email missing")

        integration.sendEmail(target.toString(), subject.toString(), message)

    }
}
