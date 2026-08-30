package br.com.designpattern.chainofresponsibility.domain.handler

import org.springframework.stereotype.Component

@Component
class FraudCheckHandler : OrderHandler() {
    override fun handle(order: Order): ValidationResult {
        // Simula verificação de fraude (exemplo: limite máximo)
        val maxOrderValue = BigDecimal("5000.00")
        return if (order.totalAmount <= maxOrderValue) {
            chain(order)
        } else {
            ValidationResult(false, "Pedido suspeito de fraude - valor muito alto")
        }
    }
}
