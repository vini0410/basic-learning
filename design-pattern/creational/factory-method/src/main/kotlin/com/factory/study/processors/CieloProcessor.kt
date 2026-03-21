package com.factory.study.processors

class CieloProcessor : PaymentProcessor {
    override fun process(amount: Double): String {
        return "Processing payment of $$amount via Cielo."
    }
}
