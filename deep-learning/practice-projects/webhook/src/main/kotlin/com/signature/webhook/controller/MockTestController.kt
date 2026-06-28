package com.signature.webhook.controller

import com.signature.webhook.feign.FeignSender
import com.signature.webhook.service.SignatureService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MockTestController(
    private val feignSender: FeignSender,
    private val signatureService: SignatureService
) {

    @GetMapping("/test-mock")
    fun testMock(): ResponseEntity<String> {
        val payload = """{"event": "study.signature", "status": "success", "data": "Hello, World!"}"""
        
        // 1. Gera a assinatura legítima usando a chave secreta
        val signature = signatureService.calculateSignature(payload)
        
        // 2. Dispara o webhook para o nosso próprio endpoint receptor
        feignSender.sendWebhook(payload, signature)

        return ResponseEntity.ok("Payload enviado e assinado com: $signature")
    }
}