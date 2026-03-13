package com.abstract.study.push

import com.abstract.study.interfaces.*

class PushSender : Sender {
    override fun send(message: String) {
        println("Enviando Push Notification: $message")
    }
}

class PushFormatter : TemplateFormatter {
    override fun format(content: String): String {
        return "{\"title\": \"Aviso\", \"body\": \"$content\"}"
    }
}

class PushLogger : ErrorLogger {
    override fun log(error: String) {
        println("[PUSH ERROR LOG]: $error")
    }
}

class PushFactory : NotificationFactory {
    override fun createSender(): Sender = PushSender()
    override fun createFormatter(): TemplateFormatter = PushFormatter()
    override fun createLogger(): ErrorLogger = PushLogger()
}
