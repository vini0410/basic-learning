package com.signature.webhook

import com.signature.webhook.service.SignatureService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebhookApplication

fun main(args: Array<String>) {
    runApplication<WebhookApplication>(*args) {
//        val signatureService = SignatureService()
//        val payload = """{"event": "test", "status": "success", "data": "Hello, World!"}"""
//        val correctSignature = signatureService.calculateSignature(payload)
//        println("Correct Signature: $correctSignature")
    }

    // Você cria um endpoint que recebe eventos de terceiros, Stripe, GitHub, Hotmart, e verifica se o payload é autêntico antes de processar.
    //A validação usa HMAC-SHA256: você compara a assinatura do header com um hash gerado com sua chave secreta. Se não bate, rejeita.
}
