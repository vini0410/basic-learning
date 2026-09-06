package com.study.ObserverApp.controller

import com.study.ObserverApp.dto.ConfirmPaymentRequest
import com.study.ObserverApp.dto.PaymentResponse
import com.study.ObserverApp.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/confirm")
    fun confirmPayment(@RequestBody request: ConfirmPaymentRequest): ResponseEntity<PaymentResponse> {
        val response = paymentService.processPayment(request)
        return ResponseEntity.ok(response)
    }
}
