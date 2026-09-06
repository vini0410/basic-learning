package com.study.MediatorApp.controller

import com.study.MediatorApp.domain.command.CancelOrderCommand
import com.study.MediatorApp.domain.command.CreateOrderCommand
import com.study.MediatorApp.domain.command.GetOrderQuery
import com.study.MediatorApp.domain.mediator.CommandMediator
import com.study.MediatorApp.dto.CancelOrderRequest
import com.study.MediatorApp.dto.CreateOrderRequest
import com.study.MediatorApp.dto.OrderResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller REST que demonstra o Mediator Pattern.
 * O Controller NAO conhece nenhum Service ou Handler especifico (Inventory, Notification, OrderRepository).
 * Ele apenas dispara Comandos para o CommandMediator.
 */
@RestController
@RequestMapping("/api/orders")
class OrderCommandController(
    private val mediator: CommandMediator
) {

    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        val command = CreateOrderCommand(
            customerId = request.customerId,
            items = request.items
        )
        val response = mediator.send(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/{id}/cancel")
    fun cancelOrder(
        @PathVariable id: String,
        @RequestBody request: CancelOrderRequest
    ): ResponseEntity<OrderResponse> {
        val command = CancelOrderCommand(
            orderId = id,
            reason = request.reason
        )
        val response = mediator.send(command)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: String): ResponseEntity<OrderResponse> {
        val query = GetOrderQuery(orderId = id)
        val response = mediator.send(query)
        return ResponseEntity.ok(response)
    }
}
