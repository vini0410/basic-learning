package com.signature.webhook.feign

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

class FeignSenderTest {

    private val feignSender = FeignSender()

    // Helper method to access private calculateHmac
    private fun calculateHmacForTest(payload: String): String {
        val method: Method = FeignSender::class.java.getDeclaredMethod("calculateHmac", String::class.java)
        method.isAccessible = true
        return method.invoke(feignSender, payload) as String
    }

    @Test
    fun `calculateHmac should produce consistent signature`() {
        val payload = "test payload"
        val expectedSignature =
            "d1867e18cefbefd2eab431f4435597fe9a8411749454746b653813bd425581fb" // Pre-calculated for "test payload" and "minha_chave_secreta_mock"

        val actualSignature = calculateHmacForTest(payload)

        assertEquals(expectedSignature, actualSignature)
    }
}
