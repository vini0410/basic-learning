package com.factory.study.service

import com.factory.study.creators.CieloCreator
import com.factory.study.creators.PayPalCreator
import com.factory.study.creators.StripeCreator
import com.factory.study.models.PaymentMethod
import com.factory.study.models.PaymentRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaymentServiceTest {
    private val creators = mapOf(
        PaymentMethod.STRIPE to StripeCreator(),
        PaymentMethod.PAYPAL to PayPalCreator(),
        PaymentMethod.CIELO to CieloCreator()
    )
    private val service = PaymentService(creators)

    @Test
    fun `should process Stripe payment`() {
        val request = PaymentRequest(PaymentMethod.STRIPE, 100.0)
        val response = service.processPayment(request)
        
        assertTrue(response.success)
        assertEquals("Processing payment of $100.0 via Stripe.", response.message)
    }

    @Test
    fun `should process PayPal payment`() {
        val request = PaymentRequest(PaymentMethod.PAYPAL, 250.0)
        val response = service.processPayment(request)
        
        assertTrue(response.success)
        assertEquals("Processing payment of $250.0 via PayPal.", response.message)
    }

    @Test
    fun `should process Cielo payment`() {
        val request = PaymentRequest(PaymentMethod.CIELO, 50.5)
        val response = service.processPayment(request)
        
        assertTrue(response.success)
        assertEquals("Processing payment of $50.5 via Cielo.", response.message)
    }
}
