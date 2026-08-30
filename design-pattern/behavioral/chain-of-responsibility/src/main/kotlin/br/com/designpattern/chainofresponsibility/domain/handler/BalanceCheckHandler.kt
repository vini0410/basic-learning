package br.com.designpattern.chainofresponsibility.domain.handler

import org.springframework.stereotype.Component

@Component
class BalanceCheckHandler : OrderHandler() {
    override fun handle(order: Order): ValidationResult {
        // Simula verificação de saldo (exemplo: mínimo de R$ 100)
        val minBalance = BigDecimal("100.00")
        return if (order.totalAmount >= minBalance) {
            chain(order)
        } else {
            ValidationResult(false, "Saldo insuficiente para este pedido")
        }
    }
}
