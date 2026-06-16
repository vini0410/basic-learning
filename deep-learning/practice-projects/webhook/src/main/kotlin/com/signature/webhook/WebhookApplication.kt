package com.signature.webhook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebhookApplication

fun main(args: Array<String>) {
    runApplication<WebhookApplication>(*args)

    // Você cria um endpoint que recebe eventos de terceiros, Stripe, GitHub, Hotmart, e verifica se o payload é autêntico antes de processar.
    //
    //A validação usa HMAC-SHA256: você compara a assinatura do header com um hash gerado com sua chave secreta. Se não bate, rejeita.
}
