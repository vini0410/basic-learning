package com.abstract.study.sms

import com.abstract.study.interfaces.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("SmsService")

class SmsSender : Sender {
    override fun send(message: String) {
        logger.info("Enviando SMS: {}", message)
    }
}

class SmsFormatter : TemplateFormatter {
    override fun format(content: String): String {
        return content.take(160).uppercase() // SMS limit and uppercase for visibility
    }
}

class SmsLogger : ErrorLogger {
    override fun log(error: String) {
        logger.error("[SMS ERROR LOG]: {}", error)
    }
}

object SmsFactory : NotificationFactory {
    override fun createSender(): Sender = SmsSender()
    override fun createFormatter(): TemplateFormatter = SmsFormatter()
    override fun createLogger(): ErrorLogger = SmsLogger()
}
