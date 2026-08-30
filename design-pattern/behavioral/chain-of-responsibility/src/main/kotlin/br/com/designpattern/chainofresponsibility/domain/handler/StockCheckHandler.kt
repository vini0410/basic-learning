package br.com.designpattern.chainofresponsibility.domain.handler

import org.springframework.stereotype.Component

@Component
class StockCheckHandler : OrderHandler() {
    override fun handle(order: Order): ValidationResult {
        // Simula verificação de estoque (exemplo: quantidade disponível)
        val availableStock = 50
        return if (order.quantity <= availableStock) {
            chain(order)
        } else {
            ValidationResult(false, "Estoque insuficiente para este produto")
        }
    }
}
