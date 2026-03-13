package com.abstract.study.interfaces

interface Sender {
    fun send(message: String)
}

interface TemplateFormatter {
    fun format(content: String): String
}

interface ErrorLogger {
    fun log(error: String)
}

interface NotificationFactory {
    fun createSender(): Sender
    fun createFormatter(): TemplateFormatter
    fun createLogger(): ErrorLogger
}
