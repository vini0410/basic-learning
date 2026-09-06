package com.study.ObserverApp.service

import com.study.ObserverApp.domain.event.OrderPaidEvent
import com.study.ObserverApp.dto.ConfirmPaymentRequest
import com.study.ObserverApp.dto.PaymentResponse
import com.study.ObserverApp.publisher.OrderEventPublisher
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class PaymentService(
    private val eventPublisher: OrderEventPublisher
) {

    fun processPayment(request: ConfirmPaymentRequest): PaymentResponse {
        require(request.orderId.isNotBlank()) { "ID do pedido nao pode ser em branco" }
        require(request.customerId.isNotBlank()) { "ID do cliente nao pode ser em branco" }
        require(request.amount > 0) { "Valor do pagamento deve ser maior que zero" }

        logger.info { "Processando pagamento do pedido '${request.orderId}' no valor de R$ ${request.amount}" }

        // 1. Instancia o evento de dominio
        val event = OrderPaidEvent(
            orderId = request.orderId,
            customerId = request.customerId,
            amount = request.amount
        )

        // 2. Notifica todos os Observers inscritos
        eventPublisher.publishOrderPaid(event)

        return PaymentResponse(
            orderId = request.orderId,
            status = "PAID",
            message = "Pagamento processado com sucesso. Eventos e notificacoes disparados."
        )
    }
}
