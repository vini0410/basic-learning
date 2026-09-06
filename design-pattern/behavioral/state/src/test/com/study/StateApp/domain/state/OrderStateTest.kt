package com.study.StateApp.domain.state

import com.study.StateApp.domain.context.OrderContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderStateTest {

    private lateinit var orderContext: OrderContext

    @BeforeEach
    fun setUp() {
        orderContext = OrderContext("ORD-1", "CUST-1", 100.0)
    }

    @Test
    fun `deve iniciar no estado PENDING_PAYMENT e permitir pagamento (happy path)`() {
        assertEquals("PENDING_PAYMENT", orderContext.currentState.getName())

        orderContext.pay()
        assertEquals("PAID", orderContext.currentState.getName())
    }

    @Test
    fun `deve seguir o ciclo completo de transicoes validas (happy path)`() {
        orderContext.pay()
        assertEquals("PAID", orderContext.currentState.getName())

        orderContext.ship()
        assertEquals("SHIPPED", orderContext.currentState.getName())

        orderContext.deliver()
        assertEquals("DELIVERED", orderContext.currentState.getName())
    }

    @Test
    fun `deve permitir cancelar pedido no estado PENDING_PAYMENT (happy path)`() {
        orderContext.cancel("Cliente desistiu")
        assertEquals("CANCELLED", orderContext.currentState.getName())
    }

    @Test
    fun `deve permitir cancelar pedido no estado PAID (happy path)`() {
        orderContext.pay()
        orderContext.cancel("Estoque esgotado")
        assertEquals("CANCELLED", orderContext.currentState.getName())
    }

    @Test
    fun `deve proibir envio de pedido nao pago (edge case - error)`() {
        val exception = assertThrows(IllegalStateException::class.java) {
            orderContext.ship()
        }
        assertEquals("Nao e possivel enviar um pedido que aguarda pagamento", exception.message)
    }

    @Test
    fun `deve proibir cancelar pedido ja enviado (edge case - error)`() {
        orderContext.pay()
        orderContext.ship()

        val exception = assertThrows(IllegalStateException::class.java) {
            orderContext.cancel("Tarde demais")
        }
        assertEquals("Nao e possivel cancelar um pedido que ja foi enviado para transporte", exception.message)
    }

    @Test
    fun `deve proibir qualquer operacao em pedido ja entregue (terminal state)`() {
        orderContext.pay()
        orderContext.ship()
        orderContext.deliver()

        assertThrows(IllegalStateException::class.java) { orderContext.pay() }
        assertThrows(IllegalStateException::class.java) { orderContext.ship() }
        assertThrows(IllegalStateException::class.java) { orderContext.deliver() }
        assertThrows(IllegalStateException::class.java) { orderContext.cancel("Motivo") }
    }
}
