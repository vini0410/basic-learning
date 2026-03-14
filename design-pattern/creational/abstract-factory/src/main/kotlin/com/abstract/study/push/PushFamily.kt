package com.abstract.study.push

import com.abstract.study.interfaces.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PushService")

class PushSender : Sender {
    override fun send(message: String) {
        logger.info("Enviando Push Notification: {}", message)
    }
}

class PushFormatter : TemplateFormatter {
    override fun format(content: String): String {
        return "{\"title\": \"Aviso\", \"body\": \"$content\"}"
    }
}

class PushLogger : ErrorLogger {
    override fun log(error: String) {
        logger.error("[PUSH ERROR LOG]: {}", error)
    }
}

object PushFactory : NotificationFactory {
    override fun createSender(): Sender = PushSender()
    override fun createFormatter(): TemplateFormatter = PushFormatter()
    override fun createLogger(): ErrorLogger = PushLogger()
}
