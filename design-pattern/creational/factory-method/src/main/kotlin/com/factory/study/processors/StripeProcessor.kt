package com.factory.study.processors

class StripeProcessor : PaymentProcessor {
    override fun process(amount: Double): String {
        return "Processing payment of $$amount via Stripe."
    }
}
