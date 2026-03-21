package com.factory.study.processors

interface PaymentProcessor {
    fun process(amount: Double): String
}
