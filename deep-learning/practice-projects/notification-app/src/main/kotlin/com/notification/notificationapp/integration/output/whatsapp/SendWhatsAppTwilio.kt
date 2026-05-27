package com.notification.notificationapp.integration.output.whatsapp

import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SendWhatsAppTwilio(
    @Value($$"${TWILIO_WHATSAPP_PHONE_NUMBER}") private val phoneNumber: String
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendMessage(phoneNumberTo: String, body: String) {
        logger.info("Sending WhatsApp message to '{}'", phoneNumberTo)
        logger.info("Sending WhatsApp message with body: '{}'", body)
        Message.creator(
            PhoneNumber("whatsapp:$phoneNumberTo"),
            PhoneNumber("whatsapp:$phoneNumber"),
            body
        ).create()
    }
}