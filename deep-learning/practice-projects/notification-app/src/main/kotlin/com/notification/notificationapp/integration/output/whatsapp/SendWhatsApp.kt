package com.notification.notificationapp.integration.output.whatsapp

import com.notification.notificationapp.core.model.WhatsAppMessageModel
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "whatsapp-client", url = "https://graph.facebook.com/v25.0")
interface SendWhatsApp {

    @PostMapping("/{phoneNumberId}/messages")
    fun sendMessage(
        @PathVariable phoneNumberId: String,
        @RequestHeader("Authorization") authorizationHeader: String,
        @RequestBody request: WhatsAppMessageModel
    ): String
}