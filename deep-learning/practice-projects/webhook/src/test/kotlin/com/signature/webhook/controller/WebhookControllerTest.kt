package com.signature.webhook.controller

import com.signature.webhook.service.SignatureService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus

class WebhookControllerTest {

    private lateinit var signatureService: SignatureService
    private lateinit var webhookController: WebhookController

    @BeforeEach
    fun setUp() {
        signatureService = mock(SignatureService::class.java)
        webhookController = WebhookController(signatureService)
    }

    @Test
    fun `receive should return OK for valid signature`() {
        val payload = "{\"event\":\"test\",\"data\":\"some data\"}"
        val validSignature = "valid_signature_hash"

        `when`(signatureService.verifySignature(payload, validSignature)).thenReturn(true)

        val response = webhookController.receive(payload, validSignature)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("OK", response.body)
        verify(signatureService, times(1)).verifySignature(payload, validSignature)
    }

    @Test
    fun `receive should return UNAUTHORIZED for invalid signature`() {
        val payload = "{\"event\":\"test\",\"data\":\"some data\"}"
        val invalidSignature = "invalid_signature_hash"

        `when`(signatureService.verifySignature(payload, invalidSignature)).thenReturn(false)

        val response = webhookController.receive(payload, invalidSignature)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Assinatura Inválida", response.body)
        verify(signatureService, times(1)).verifySignature(payload, invalidSignature)
    }

    @Test
    fun `receive should return UNAUTHORIZED for tampered payload`() {
        val originalPayload = "{\"event\":\"test\",\"data\":\"some data\"}"
        val validSignature = "valid_signature_hash" // This signature was generated for originalPayload

        val tamperedPayload = "{\"event\":\"test\",\"data\":\"tampered data\"}"

        // Even if the signature was "valid" for the original payload,
        // when verified against the tampered payload, it should be invalid.
        `when`(signatureService.verifySignature(tamperedPayload, validSignature)).thenReturn(false)

        val response = webhookController.receive(tamperedPayload, validSignature)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Assinatura Inválida", response.body)
        verify(signatureService, times(1)).verifySignature(tamperedPayload, validSignature)
    }
}