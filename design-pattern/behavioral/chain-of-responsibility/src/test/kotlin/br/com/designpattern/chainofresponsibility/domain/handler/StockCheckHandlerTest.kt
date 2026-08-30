package br.com.designpattern.chainofresponsibility.domain.handler

import org.junit.jupiter.api.Test

class StockCheckHandlerTest {
    private val handler = StockCheckHandler()

    @Test
    fun `should approve order with sufficient stock`() {
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("200.00"))
        
        val result = handler.handle(order)
        
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `should reject order with insufficient stock`() {
        val order = Order(customerId = 1, productId = 100, quantity = 100, totalAmount = BigDecimal("200.00"))
        
        val result = handler.handle(order)
        
        assertThat(result.isValid).isFalse()
        assertThat(result.message).isEqualTo("Estoque insuficiente para este produto")
    }
}
