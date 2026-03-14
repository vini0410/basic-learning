package com.abstract.study

import com.abstract.study.email.EmailFactory
import com.abstract.study.interfaces.NotificationFactory
import com.abstract.study.models.*
import com.abstract.study.plugins.*
import com.abstract.study.push.PushFactory
import com.abstract.study.service.NotificationService
import com.abstract.study.sms.SmsFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {

    val notificationFactories: Map<ChannelType, NotificationFactory> = mapOf(
        ChannelType.EMAIL to EmailFactory,
        ChannelType.SMS to SmsFactory,
        ChannelType.PUSH to PushFactory
    )
    
    val service = NotificationService(notificationFactories)

    embeddedServer(Netty, port = 8080) {
        configureSerialization()
        configureExceptionHandling()
        configureRouting(service)
    }.start(wait = true)
}
