package com.abstract.study.email

import com.abstract.study.interfaces.*

class EmailSender : Sender {
    override fun send(message: String) {
        println("Enviando Email: $message")
    }
}

class EmailFormatter : TemplateFormatter {
    override fun format(content: String): String {
        return "<html><body>$content</body></html>"
    }
}

class EmailLogger : ErrorLogger {
    override fun log(error: String) {
        println("[EMAIL ERROR LOG]: $error")
    }
}

class EmailFactory : NotificationFactory {
    override fun createSender(): Sender = EmailSender()
    override fun createFormatter(): TemplateFormatter = EmailFormatter()
    override fun createLogger(): ErrorLogger = EmailLogger()
}
