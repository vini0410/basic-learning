package com.factory.study.plugins

import com.factory.study.models.PaymentRequest
import com.factory.study.service.PaymentService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(service: PaymentService) {
    routing {
        post("/pay") {
            val request = call.receive<PaymentRequest>()
            val result = service.processPayment(request)
            call.respond(result)
        }
    }
}
