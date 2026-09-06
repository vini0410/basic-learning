package com.study.ObserverApp.domain.observer

import com.study.ObserverApp.domain.event.OrderPaidEvent
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

/**
 * Interface customizada do Observador (Observer Pattern).
 */
interface OrderObserver {
    fun onOrderPaid(event: OrderPaidEvent)
}

@Component
class EmailNotificationObserver : OrderObserver {
    @EventListener
    override fun onOrderPaid(event: OrderPaidEvent) {
        logger.info { "[EmailNotificationObserver] Email de confirmacao enviado para o cliente '${event.customerId}' referente ao pedido '${event.orderId}'. Valor: R$ ${event.amount}" }
    }
}

@Component
class InventoryUpdateObserver : OrderObserver {
    @EventListener
    override fun onOrderPaid(event: OrderPaidEvent) {
        logger.info { "[InventoryUpdateObserver] Estoque atualizado e baixa efetuada para o pedido '${event.orderId}'" }
    }
}

@Component
class FiscalInvoiceObserver : OrderObserver {
    @EventListener
    override fun onOrderPaid(event: OrderPaidEvent) {
        logger.info { "[FiscalInvoiceObserver] Nota fiscal emitida com sucesso para o pedido '${event.orderId}'" }
    }
}
