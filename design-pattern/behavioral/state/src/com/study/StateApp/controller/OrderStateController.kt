package com.study.StateApp.controller

import com.study.StateApp.dto.CancelOrderRequest
import com.study.StateApp.dto.CreateOrderRequest
import com.study.StateApp.dto.OrderResponse
import com.study.StateApp.service.OrderLifecycleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderStateController(
    private val orderService: OrderLifecycleService
) {

    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        val response = orderService.createOrder(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: String): ResponseEntity<OrderResponse> {
        val response = orderService.getOrder(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/pay")
    fun payOrder(@PathVariable id: String): ResponseEntity<OrderResponse> {
        val response = orderService.payOrder(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/ship")
    fun shipOrder(@PathVariable id: String): ResponseEntity<OrderResponse> {
        val response = orderService.shipOrder(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/deliver")
    fun deliverOrder(@PathVariable id: String): ResponseEntity<OrderResponse> {
        val response = orderService.deliverOrder(id)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/cancel")
    fun cancelOrder(
        @PathVariable id: String,
        @RequestBody request: CancelOrderRequest
    ): ResponseEntity<OrderResponse> {
        val response = orderService.cancelOrder(id, request.reason)
        return ResponseEntity.ok(response)
    }
}
