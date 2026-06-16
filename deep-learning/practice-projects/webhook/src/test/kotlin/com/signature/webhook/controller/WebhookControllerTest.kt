package com.signature.webhook.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.lang.reflect.Method

class WebhookControllerTest {

    private val webhookController = WebhookController()

    // Helper method to access private calculateHmac
    private fun calculateHmacForTest(payload: String): String {
        val method: Method = WebhookController::class.java.getDeclaredMethod("calculateHmac", String::class.java)
        method.isAccessible = true
        return method.invoke(webhookController, payload) as String
    }

    @Test
    fun `handleWebhook should return OK for valid signature`() {
        val payload = "{\"event\":\"test\",\"data\":\"some data\"}"
        val validSignature = calculateHmacForTest(payload)

        val response = webhookController.handleWebhook(payload, validSignature)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Webhook received and processed successfully!", response.body)
    }

    @Test
    fun `handleWebhook should return BAD_REQUEST for invalid signature`() {
        val payload = "{\"event\":\"test\",\"data\":\"some data\"}"
        val invalidSignature = "invalid_signature"

        val response = webhookController.handleWebhook(payload, invalidSignature)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid webhook signature", response.body)
    }

    @Test
    fun `handleWebhook should return BAD_REQUEST for tampered payload`() {
        val originalPayload = "{\"event\":\"test\",\"data\":\"some data\"}"
        val validSignature = calculateHmacForTest(originalPayload)

        val tamperedPayload = "{\"event\":\"test\",\"data\":\"tampered data\"}"

        val response = webhookController.handleWebhook(tamperedPayload, validSignature)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid webhook signature", response.body)
    }

    @Test
    fun `handleWebhook should return INTERNAL_SERVER_ERROR for unexpected exception`() {
        // Simulate an exception by passing null, which will cause NullPointerException
        // Note: In a real scenario, you might mock dependencies to throw exceptions
        val payload = "some payload"
        val signature = "some signature"

        // Temporarily change the secretKey to null to force an exception in calculateHmac
        // This is a hack for testing purposes, ideally you'd mock the Mac.getInstance or similar
        val field = WebhookController::class.java.getDeclaredField("secretKey")
        field.isAccessible = true
        val originalSecretKey = field.get(webhookController)
        field.set(webhookController, null) // Set to null to cause NPE

        try {
            val response = webhookController.handleWebhook(payload, signature)
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
            // The message might vary depending on the exact exception, but it should not be null
            assert(response.body != null)
        } finally {
            // Restore the original secretKey
            field.set(webhookController, originalSecretKey)
        }
    }

    @Test
    fun `calculateHmac should produce consistent signature`() {
        val payload = "test payload"
        val expectedSignature =
            "d1867e18cefbefd2eab431f4435597fe9a8411749454746b653813bd425581fb" // Corrected signature

        val actualSignature = calculateHmacForTest(payload)

        assertEquals(expectedSignature, actualSignature)
    }

    @Test
    fun `constantTimeEquals should return true for equal strings`() {
        val method: Method = WebhookController::class.java.getDeclaredMethod(
            "constantTimeEquals",
            String::class.java,
            String::class.java
        )
        method.isAccessible = true
        val result = method.invoke(webhookController, "abc", "abc") as Boolean
        assertEquals(true, result)
    }

    @Test
    fun `constantTimeEquals should return false for different strings`() {
        val method: Method = WebhookController::class.java.getDeclaredMethod(
            "constantTimeEquals",
            String::class.java,
            String::class.java
        )
        method.isAccessible = true
        val result = method.invoke(webhookController, "abc", "abd") as Boolean
        assertEquals(false, result)
    }

    @Test
    fun `constantTimeEquals should return false for strings of different lengths`() {
        val method: Method = WebhookController::class.java.getDeclaredMethod(
            "constantTimeEquals",
            String::class.java,
            String::class.java
        )
        method.isAccessible = true
        val result = method.invoke(webhookController, "abc", "abcd") as Boolean
        assertEquals(false, result)
    }
}
