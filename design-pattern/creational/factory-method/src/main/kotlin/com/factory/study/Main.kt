package com.factory.study

import com.factory.study.plugins.*
import com.factory.study.service.PaymentService
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val service = PaymentService()

    embeddedServer(Netty, port = 8080) {
        configureSerialization()
        configureExceptionHandling()
        configureRouting(service)
    }.start(wait = true)
}
