package com.notification.notificationapp.integration.output.email

import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class SendEmail(
    private val sender: JavaMailSender
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun sendEmail(targetEmail: String, subject: String, body: String) {
        logger.info("Sending email to '{}'", targetEmail)
        logger.info("Sending email with '{}' -- body: '{}'", subject, body)
        val mailMessage = SimpleMailMessage().apply {
            setTo(targetEmail)
            setSubject(subject)
            text = body
        }

        sender.send(mailMessage)
    }
}