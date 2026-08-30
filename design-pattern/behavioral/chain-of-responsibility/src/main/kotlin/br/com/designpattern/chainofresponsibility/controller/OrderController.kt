package br.com.designpattern.chainofresponsibility.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderValidationService: OrderValidationService) {
    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<*> {
        val order = Order(
            customerId = request.customerId,
            productId = request.productId,
            quantity = request.quantity,
            totalAmount = BigDecimal(request.totalAmount)
        )

        val validationResult = orderValidationService.validateOrder(order)

        return if (validationResult.isValid) {
            ResponseEntity.ok(mapOf("message" to "Pedido aprovado", "orderId" to order.id))
        } else {
            ResponseEntity.badRequest().body(mapOf("error" to validationResult.message))
        }
    }
}

data class CreateOrderRequest(
    val customerId: Long,
    val productId: Long,
    val quantity: Int,
    val totalAmount: String
)
