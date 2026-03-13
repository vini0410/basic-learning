package com.abstract.study

import com.abstract.study.email.EmailFactory
import com.abstract.study.models.ChannelType
import com.abstract.study.models.NotificationRequest
import com.abstract.study.push.PushFactory
import com.abstract.study.service.NotificationService
import com.abstract.study.sms.SmsFactory
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    // Configuração das fábricas (Injeção de Dependência Manual)
    val notificationFactories = mapOf(
        ChannelType.EMAIL to EmailFactory,
        ChannelType.SMS to SmsFactory,
        ChannelType.PUSH to PushFactory
    )
    
    val service = NotificationService(notificationFactories)

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            post("/notify") {
                try {
                    val request = call.receive<NotificationRequest>()
                    val result = service.sendNotification(request)
                    call.respond(result)
                } catch (e: Exception) {
                    call.respond(mapOf("error" to (e.message ?: "Invalid request")))
                }
            }
        }
    }.start(wait = true)
}
