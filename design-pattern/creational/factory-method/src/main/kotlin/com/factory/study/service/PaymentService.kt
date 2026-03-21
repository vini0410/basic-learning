package com.factory.study.service

import com.factory.study.creators.PaymentCreator
import com.factory.study.models.PaymentMethod
import com.factory.study.models.PaymentRequest
import com.factory.study.models.PaymentResponse
import com.factory.study.exceptions.GeneralException
import com.factory.study.exceptions.ErrorType

class PaymentService(
    private val creators: Map<PaymentMethod, PaymentCreator>
) {
    fun processPayment(request: PaymentRequest): PaymentResponse {
        val creator = creators[request.method] 
            ?: throw GeneralException(ErrorType.INVALID_PAYLOAD, "Método de pagamento não suportado.")
        
        val message = creator.processPayment(request.amount)
        
        return PaymentResponse(
            success = true,
            message = message
        )
    }
}
