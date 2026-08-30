package br.com.designpattern.chainofresponsibility.domain.handler

import org.springframework.stereotype.Component

@Component
class AddressCheckHandler : OrderHandler() {
    override fun handle(order: Order): ValidationResult {
        // Simula verificação de endereço (exemplo: CEP válido)
        val validCepPattern = "^\d{8}$".toRegex()
        return if (validCepPattern.matches("12345678")) { // Exemplo de CEP válido
            chain(order)
        } else {
            ValidationResult(false, "Endereço inválido")
        }
    }
}
