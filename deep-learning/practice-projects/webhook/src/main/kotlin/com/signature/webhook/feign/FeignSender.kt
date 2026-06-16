package com.signature.webhook.feign

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class FeignSender {

    private val webhookUrl = "http://localhost:8080/webhook"

    private val secretKey = "minha_chave_secreta_mock"

    fun sendMessage(payload: String) {

        val signature = calculateHmac(payload)

        val client = HttpClient.newHttpClient()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("x-signature", signature)
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build()
        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            println("Status: ${response.statusCode()}")
            println("Response: ${response.body()}")
        } catch (e: Exception) {
            println("Error: ${e.message}")
            throw e
        }
    }

    fun calculateHmac(payload: String): String {
        val keySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)
        val rawHmac = mac.doFinal(payload.toByteArray())
        return rawHmac.joinToString("") { "%02x".format(it) }
    }
}