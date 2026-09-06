package com.study.StrategyApp.controller

import com.study.StrategyApp.dto.CalculateDiscountRequest
import com.study.StrategyApp.dto.CalculateDiscountResponse
import com.study.StrategyApp.service.CheckoutService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/checkout")
class CheckoutController(
    private val checkoutService: CheckoutService
) {

    @PostMapping("/calculate")
    fun calculateDiscount(@RequestBody request: CalculateDiscountRequest): ResponseEntity<CalculateDiscountResponse> {
        val response = checkoutService.calculateCheckout(request)
        return ResponseEntity.ok(response)
    }
}
