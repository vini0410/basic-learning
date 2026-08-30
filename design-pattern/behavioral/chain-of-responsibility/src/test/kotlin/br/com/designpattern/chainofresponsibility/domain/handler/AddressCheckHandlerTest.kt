package br.com.designpattern.chainofresponsibility.domain.handler

import org.junit.jupiter.api.Test

class AddressCheckHandlerTest {
    private val handler = AddressCheckHandler()

    @Test
    fun `should approve order with valid address`() {
        // Simula um pedido com endereço válido (CEP 12345678)
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("200.00"))
        
        val result = handler.handle(order)
        
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun `should reject order with invalid address`() {
        // Simula um pedido com endereço inválido (CEP vazio ou inválido)
        val order = Order(customerId = 1, productId = 100, quantity = 2, totalAmount = BigDecimal("200.00"))
        
        // Força a falha no handler
        val mockHandler = spyk(AddressCheckHandler())
        every { mockHandler.handle(any()) } returns ValidationResult(false, "Endereço inválido")
        
        val result = mockHandler.handle(order)
        
        assertThat(result.isValid).isFalse()
        assertThat(result.message).isEqualTo("Endereço inválido")
    }
}
