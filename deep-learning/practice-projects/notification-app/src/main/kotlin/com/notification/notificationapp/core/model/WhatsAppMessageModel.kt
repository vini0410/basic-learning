package com.notification.notificationapp.core.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WhatsAppMessageModel(
    @JsonProperty("messaging_product")
    val mesagingProduct: String = "whatsapp",

    val to: String,

    val type: String = "text",

    val text: WhatsAppBody
)

data class WhatsAppBody(
    val body : String
)
