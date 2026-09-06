package com.study.ObserverApp.publisher

import com.study.ObserverApp.domain.event.OrderPaidEvent
import com.study.ObserverApp.domain.observer.OrderObserver
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.util.concurrent.CopyOnWriteArrayList

private val logger = KotlinLogging.logger {}

/**
 * Subject / Emissor de Eventos.
 * Permite inscricao manual de Observers e tambem integra com Spring ApplicationEventPublisher.
 */
@Component
class OrderEventPublisher(
    private val springPublisher: ApplicationEventPublisher,
    observers: List<OrderObserver> = emptyList()
) {
    private val registeredObservers = CopyOnWriteArrayList<OrderObserver>(observers)

    fun registerObserver(observer: OrderObserver) {
        registeredObservers.add(observer)
        logger.info { "Novo observer registrado: ${observer.javaClass.simpleName}" }
    }

    fun removeObserver(observer: OrderObserver) {
        registeredObservers.remove(observer)
        logger.info { "Observer removido: ${observer.javaClass.simpleName}" }
    }

    fun publishOrderPaid(event: OrderPaidEvent) {
        logger.info { "Publicando evento OrderPaidEvent para o pedido '${event.orderId}'" }

        // 1. Notifica observadores registrados manualmente
        registeredObservers.forEach { observer ->
            try {
                observer.onOrderPaid(event)
            } catch (ex: Exception) {
                logger.error(ex) { "Erro ao notificar o observer ${observer.javaClass.simpleName}" }
            }
        }

        // 2. Dispara evento no barramento Spring
        springPublisher.publishEvent(event)
    }

    fun getObserverCount(): Int = registeredObservers.size
}
