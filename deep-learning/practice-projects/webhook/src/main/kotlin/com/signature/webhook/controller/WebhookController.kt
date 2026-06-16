package com.signature.webhook.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@RestController
class WebhookController {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val secretKey = "minha_chave_secreta_mock"

    @PostMapping("/webhook")
    fun handleWebhook(
        @RequestBody payload: String,
        @RequestHeader("x-signature") signature: String
    ): ResponseEntity<String> {
        try {
            val isValid = validateSignature(payload, signature)

            if (isValid) {
                logger.info("SUCCESS")
                return ResponseEntity.ok("Webhook received and processed successfully!")
            } else {
                logger.error("INVALID")
                return ResponseEntity.badRequest().body("Invalid webhook signature")
            }
        } catch (e: Exception) {
            logger.error("ERROR", e)
            return ResponseEntity.status(500).body(e.message)
        }
    }

    private fun validateSignature(payload: String, signature: String): Boolean {
        val calculatesSignature = calculateHmac(payload)

        return constantTimeEquals(calculatesSignature, signature)
    }

    private fun calculateHmac(payload: String): String {
        val keySpec = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)
        val rawMac = mac.doFinal(payload.toByteArray())

        return rawMac.joinToString("") { "%02x".format(it) }
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) {
            return false
        }
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }
}