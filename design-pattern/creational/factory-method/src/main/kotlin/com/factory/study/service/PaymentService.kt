package com.factory.study.service

import com.factory.study.models.PaymentRequest
import com.factory.study.models.PaymentResponse

class PaymentService {
    fun processPayment(request: PaymentRequest): PaymentResponse {
        val creator = request.method.creator
        val message = creator.processPayment(request.amount)
        
        return PaymentResponse(
            success = true,
            message = message
        )
    }
}
