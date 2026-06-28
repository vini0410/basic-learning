package com.signature.webhook.controller

import com.signature.webhook.service.SignatureService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhooks")
class WebhookController(private val signatureService: SignatureService) {

    @PostMapping("/receive")
    fun receive(
        @RequestBody payload: String,
        @RequestHeader("X-Signature") signature: String
    ): ResponseEntity<String> {
        println("Evento recebido: $payload | $signature")
        if (!signatureService.verifySignature(payload, signature)) {
            println("Assinatura Inválida")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Assinatura Inválida")
        }

        println("Evento recebido com sucesso: $payload")
        return ResponseEntity.ok("OK")
    }
}