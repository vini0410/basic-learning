package com.factory.study

import com.factory.study.creators.*
import com.factory.study.models.PaymentMethod
import com.factory.study.plugins.*
import com.factory.study.service.PaymentService
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val creators = mapOf(
        PaymentMethod.STRIPE to StripeCreator(),
        PaymentMethod.PAYPAL to PayPalCreator(),
        PaymentMethod.CIELO to CieloCreator()
    )
    
    val service = PaymentService(creators)

    embeddedServer(Netty, port = 8080) {
        configureSerialization()
        configureExceptionHandling()
        configureRouting(service)
    }.start(wait = true)
}
