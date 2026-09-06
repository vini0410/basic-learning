package com.study.ObserverApp.service

import com.study.ObserverApp.domain.event.OrderPaidEvent
import com.study.ObserverApp.dto.ConfirmPaymentRequest
import com.study.ObserverApp.publisher.OrderEventPublisher
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PaymentServiceTest {

    private lateinit var eventPublisher: OrderEventPublisher
    private lateinit var paymentService: PaymentService

    @BeforeEach
    fun setUp() {
        eventPublisher = mockk()
        paymentService = PaymentService(eventPublisher)
    }

    @Test
    fun `deve processar pagamento e publicar evento de pedido pago com sucesso (happy path)`() {
        val request = ConfirmPaymentRequest("ORD-100", "CUST-100", 150.0)
        every { eventPublisher.publishOrderPaid(any()) } just runs

        val response = paymentService.processPayment(request)

        assertEquals("ORD-100", response.orderId)
        assertEquals("PAID", response.status)

        verify(exactly = 1) {
            eventPublisher.publishOrderPaid(match {
                it.orderId == "ORD-100" && it.customerId == "CUST-100" && it.amount == 150.0
            })
        }
    }

    @Test
    fun `deve lancar IllegalArgumentException se o valor do pagamento for menor ou igual a zero (edge case)`() {
        val request = ConfirmPaymentRequest("ORD-100", "CUST-100", 0.0)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            paymentService.processPayment(request)
        }
        assertEquals("Valor do pagamento deve ser maior que zero", exception.message)
    }
}
