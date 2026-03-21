package com.factory.study.creators

import com.factory.study.processors.PaymentProcessor
import com.factory.study.processors.PayPalProcessor

class PayPalCreator : PaymentCreator() {
    override fun createProcessor(): PaymentProcessor = PayPalProcessor()
}
