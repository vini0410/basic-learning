package com.study.ObserverApp.publisher

import com.study.ObserverApp.domain.event.OrderPaidEvent
import com.study.ObserverApp.domain.observer.OrderObserver
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

class OrderEventPublisherTest {

    private lateinit var springPublisher: ApplicationEventPublisher
    private lateinit var eventPublisher: OrderEventPublisher
    private lateinit var mockObserver1: OrderObserver
    private lateinit var mockObserver2: OrderObserver

    @BeforeEach
    fun setUp() {
        springPublisher = mockk(relaxed = true)
        mockObserver1 = mockk()
        mockObserver2 = mockk()
        eventPublisher = OrderEventPublisher(springPublisher, listOf(mockObserver1, mockObserver2))
    }

    @Test
    fun `deve notificar todos os observadores inscritos ao publicar evento (happy path)`() {
        val event = OrderPaidEvent("ORD-100", "CUST-100", 250.0)

        every { mockObserver1.onOrderPaid(event) } just runs
        every { mockObserver2.onOrderPaid(event) } just runs

        eventPublisher.publishOrderPaid(event)

        verify(exactly = 1) { mockObserver1.onOrderPaid(event) }
        verify(exactly = 1) { mockObserver2.onOrderPaid(event) }
        verify(exactly = 1) { springPublisher.publishEvent(event) }
    }

    @Test
    fun `deve permitir registrar e remover observadores dinamicamente (happy path)`() {
        val newObserver = mockk<OrderObserver>()
        assertEquals(2, eventPublisher.getObserverCount())

        eventPublisher.registerObserver(newObserver)
        assertEquals(3, eventPublisher.getObserverCount())

        eventPublisher.removeObserver(newObserver)
        assertEquals(2, eventPublisher.getObserverCount())
    }

    @Test
    fun `deve continuar notificando outros observadores mesmo se um deles falhar (edge case)`() {
        val event = OrderPaidEvent("ORD-100", "CUST-100", 250.0)

        every { mockObserver1.onOrderPaid(event) } throws RuntimeException("Erro envio email")
        every { mockObserver2.onOrderPaid(event) } just runs

        eventPublisher.publishOrderPaid(event)

        verify(exactly = 1) { mockObserver1.onOrderPaid(event) }
        verify(exactly = 1) { mockObserver2.onOrderPaid(event) }
    }
}
