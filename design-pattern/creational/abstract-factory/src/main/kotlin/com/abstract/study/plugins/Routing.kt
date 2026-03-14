package com.abstract.study.plugins

import com.abstract.study.models.NotificationRequest
import com.abstract.study.service.NotificationService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(service: NotificationService) {
    routing {
        post("/notify") {
            val request = call.receive<NotificationRequest>()
            val result = service.sendNotification(request)
            call.respond(result)
        }
    }
}
