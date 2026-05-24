package com.notification.notificationapp.integration.output.sms

import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SendSms(
    @Value($$"${twilio.sms.phone-number}") private val phoneNumberFrom: String
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendSMS(phoneNumberTo: String, body: String) {
        logger.info("Sending SMS to '{}'", phoneNumberTo)
        logger.info("Sending SMS with body: '{}'", body)
        val message = Message.creator(
            PhoneNumber(phoneNumberTo),
            PhoneNumber(phoneNumberFrom),
            body
        ).create()
    }
}