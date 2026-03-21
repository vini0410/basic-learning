package com.factory.study.creators

import com.factory.study.processors.PaymentProcessor
import com.factory.study.processors.StripeProcessor

class StripeCreator : PaymentCreator() {
    override fun createProcessor(): PaymentProcessor = StripeProcessor()
}
