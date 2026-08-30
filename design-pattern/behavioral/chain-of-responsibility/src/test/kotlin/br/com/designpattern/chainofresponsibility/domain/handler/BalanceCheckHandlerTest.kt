package br.com.designpattern.chainofresponsibility.domain.handler

import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BalanceCheckHandlerTest {
    private val handler = BalanceCheckHandler()

    @Test
    fun `should approve order with sufficient balance`() {
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("200.00"))
        
        val result = handler.handle(order)
        
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `should reject order with insufficient balance`() {
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("50.00"))
        
        val result = handler.handle(order)
        
        assertThat(result.isValid).isFalse()
        assertThat(result.message).isEqualTo("Saldo insuficiente para este pedido")
    }
}
