package com.notification.notificationapp.integration.config

import com.twilio.Twilio
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class TwilioConfig(
    @Value($$"${twilio.sms.accountSid}") private val accountSid: String,
    @Value($$"${twilio.sms.authToken}") private val authToken: String
) {

    init {
        Twilio.init(accountSid, authToken)
    }
}