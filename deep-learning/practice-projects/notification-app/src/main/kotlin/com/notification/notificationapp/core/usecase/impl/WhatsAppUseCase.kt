package com.notification.notificationapp.core.usecase.impl

import com.notification.notificationapp.core.model.WhatsAppBody
import com.notification.notificationapp.core.model.WhatsAppMessageModel
import com.notification.notificationapp.core.usecase.NotificationUseCase
import com.notification.notificationapp.integration.output.whatsapp.SendWhatsApp
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class WhatsAppUseCase(
    private val integration: SendWhatsApp,
    @Value($$"${meta.whatsapp.token}") private val token: String,
    @Value($$"${meta.whatsapp.number-id}") private val phoneNumberId: String
) : NotificationUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun sendNotification(metadata: Map<String, Any>, message: String) {
        logger.info("useCase: received: '{}' -- '{}'", metadata, message)

        val target = metadata["target"] ?: throw RuntimeException("target WhatsApp number missing")

        val payload = WhatsAppMessageModel(to = target.toString(), text = WhatsAppBody(message))

        try {
            val response = integration.sendMessage(phoneNumberId, "Bearer $token", payload)
            logger.info("response: '{}' -- '{}'", target, response)
        } catch (e: Exception) {
            logger.error("Error while sending whatsApp message", e)
            throw RuntimeException("Error sending whatsApp message", e)
        }
    }
}