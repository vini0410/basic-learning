package com.factory.study.creators

import com.factory.study.processors.PaymentProcessor

abstract class PaymentCreator {
    // Factory Method
    abstract fun createProcessor(): PaymentProcessor

    // Business Logic using the Factory Method
    fun processPayment(amount: Double): String {
        val processor = createProcessor()
        return processor.process(amount)
    }
}
