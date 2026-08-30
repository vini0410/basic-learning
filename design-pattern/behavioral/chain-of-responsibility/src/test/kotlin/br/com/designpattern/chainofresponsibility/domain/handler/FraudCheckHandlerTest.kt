package br.com.designpattern.chainofresponsibility.domain.handler

import org.junit.jupiter.api.Test

class FraudCheckHandlerTest {
    private val handler = FraudCheckHandler()

    @Test
    fun `should approve order within fraud limit`() {
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("200.00"))
        
        val result = handler.handle(order)
        
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `should reject order exceeding fraud limit`() {
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("6000.00"))
        
        val result = handler.handle(order)
        
        assertThat(result.isValid).isFalse()
        assertThat(result.message).isEqualTo("Pedido suspeito de fraude - valor muito alto")
    }
}
