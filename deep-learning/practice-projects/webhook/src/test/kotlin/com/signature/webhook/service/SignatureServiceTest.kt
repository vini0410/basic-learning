package com.signature.webhook.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SignatureServiceTest {

    private val signatureService = SignatureService()

    @Test
    fun `should validate a correct signature`() {
        val payload = """{"event":"test"}"""
        val signature = signatureService.calculateSignature(payload)
        
        val isValid = signatureService.verifySignature(payload, signature)
        
        assertTrue(isValid, "A assinatura deveria ser válida para o payload original")
    }

    @Test
    fun `should reject signature if payload is tampered`() {
        val payload = """{"event":"test"}"""
        val tamperedPayload = """{"event":"test", "hacked": true}"""
        val signature = signatureService.calculateSignature(payload)
        
        val isValid = signatureService.verifySignature(tamperedPayload, signature)
        
        assertFalse(isValid, "A assinatura deveria ser inválida para um payload alterado")
    }

    @Test
    fun `should reject an invalid signature string`() {
        val payload = """{"event":"test"}"""
        val invalidSignature = "invalid_hash"
        
        val isValid = signatureService.verifySignature(payload, invalidSignature)
        
        assertFalse(isValid, "Uma assinatura malformada deve ser rejeitada")
    }
}