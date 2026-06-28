package com.signature.webhook.feign

import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class FeignSender(private val httpClient: HttpClient = HttpClient.newBuilder().build()) {

    fun sendWebhook(payload: String, signature: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/webhooks/receive"))
            .header("Content-Type", "application/json")
            .header("X-Signature", signature)
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        println("Status do envio: ${response.statusCode()}")
    }
}