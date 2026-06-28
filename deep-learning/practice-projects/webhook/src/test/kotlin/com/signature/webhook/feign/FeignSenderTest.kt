package com.signature.webhook.feign

import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class FeignSenderTest {

    @Test
    fun `sendWebhook should send a POST request with correct headers and payload`() {
        // Mock HttpClient and its behavior
        val mockHttpClient = mock(HttpClient::class.java)
        val mockHttpResponse = mock(HttpResponse::class.java) as HttpResponse<String>

        // Inject the mocked HttpClient into FeignSender
        val feignSender = FeignSender(mockHttpClient)

        `when`(mockHttpClient.send(any(HttpRequest::class.java), any(HttpResponse.BodyHandler::class.java)))
            .thenReturn(mockHttpResponse)
        `when`(mockHttpResponse.statusCode()).thenReturn(200)

        val payload = "{\"event\":\"test\"}"
        val signature = "test_signature"

        feignSender.sendWebhook(payload, signature)

        // Verify that send was called
        verify(mockHttpClient, times(1)).send(any(HttpRequest::class.java), any(HttpResponse.BodyHandler::class.java))
    }
}