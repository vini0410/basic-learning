package com.abstract.study.email

import com.abstract.study.interfaces.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("EmailService")

class EmailSender : Sender {
    override fun send(message: String) {
        logger.info("Enviando Email: {}", message)
    }
}

class EmailFormatter : TemplateFormatter {
    override fun format(content: String): String {
        return "<html><body>$content</body></html>"
    }
}

class EmailLogger : ErrorLogger {
    override fun log(error: String) {
        logger.error("[EMAIL ERROR LOG]: {}", error)
    }
}

object EmailFactory : NotificationFactory {
    override fun createSender(): Sender = EmailSender()
    override fun createFormatter(): TemplateFormatter = EmailFormatter()
    override fun createLogger(): ErrorLogger = EmailLogger()
}
