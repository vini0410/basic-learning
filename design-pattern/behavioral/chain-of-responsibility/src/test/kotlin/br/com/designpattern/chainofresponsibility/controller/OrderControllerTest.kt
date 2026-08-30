package br.com.designpattern.chainofresponsibility.controller

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class OrderControllerTest {
    private val orderValidationService = mockk<OrderValidationService>()
    private val controller = OrderController(orderValidationService)

    @Test
    fun `should return 200 OK when order is valid`() {
        every { orderValidationService.validateOrder(any()) } returns ValidationResult(true, "OK")
        
        val request = CreateOrderRequest(customerId = 1, productId = 100, quantity = 2, totalAmount = "200.00")
        
        val response = controller.createOrder(request)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should return 400 BAD_REQUEST when order is invalid`() {
        every { orderValidationService.validateOrder(any()) } returns ValidationResult(false, "Saldo insuficiente")
        
        val request = CreateOrderRequest(customerId = 1, productId = 100, quantity = 2, totalAmount = "50.00")
        
        val response = controller.createOrder(request)
        
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}
