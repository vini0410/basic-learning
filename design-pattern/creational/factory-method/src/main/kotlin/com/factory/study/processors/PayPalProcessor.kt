package com.factory.study.processors

class PayPalProcessor : PaymentProcessor {
    override fun process(amount: Double): String {
        return "Processing payment of $$amount via PayPal."
    }
}
