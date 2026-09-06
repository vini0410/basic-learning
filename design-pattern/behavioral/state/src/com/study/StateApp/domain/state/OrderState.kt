package com.study.StateApp.domain.state

import com.study.StateApp.domain.context.OrderContext

/**
 * Interface do Padrão State.
 * Cada estado implementa as regras e transicoes validas para seu contexto.
 */
interface OrderState {
    fun getName(): String
    fun pay(context: OrderContext)
    fun ship(context: OrderContext)
    fun deliver(context: OrderContext)
    fun cancel(context: OrderContext, reason: String)
}

class PendingPaymentState : OrderState {
    override fun getName(): String = "PENDING_PAYMENT"

    override fun pay(context: OrderContext) {
        context.changeState(PaidState())
    }

    override fun ship(context: OrderContext) {
        throw IllegalStateException("Nao e possivel enviar um pedido que aguarda pagamento")
    }

    override fun deliver(context: OrderContext) {
        throw IllegalStateException("Nao e possivel entregar um pedido que aguarda pagamento")
    }

    override fun cancel(context: OrderContext, reason: String) {
        context.changeState(CancelledState(reason))
    }
}

class PaidState : OrderState {
    override fun getName(): String = "PAID"

    override fun pay(context: OrderContext) {
        throw IllegalStateException("Pedido ja esta pago")
    }

    override fun ship(context: OrderContext) {
        context.changeState(ShippedState())
    }

    override fun deliver(context: OrderContext) {
        throw IllegalStateException("Nao e possivel entregar um pedido pago que ainda nao foi enviado")
    }

    override fun cancel(context: OrderContext, reason: String) {
        context.changeState(CancelledState(reason))
    }
}

class ShippedState : OrderState {
    override fun getName(): String = "SHIPPED"

    override fun pay(context: OrderContext) {
        throw IllegalStateException("Pedido ja foi pago e enviado")
    }

    override fun ship(context: OrderContext) {
        throw IllegalStateException("Pedido ja foi enviado")
    }

    override fun deliver(context: OrderContext) {
        context.changeState(DeliveredState())
    }

    override fun cancel(context: OrderContext, reason: String) {
        throw IllegalStateException("Nao e possivel cancelar um pedido que ja foi enviado para transporte")
    }
}

class DeliveredState : OrderState {
    override fun getName(): String = "DELIVERED"

    override fun pay(context: OrderContext) = throw IllegalStateException("Pedido ja foi entregue ao cliente")
    override fun ship(context: OrderContext) = throw IllegalStateException("Pedido ja foi entregue ao cliente")
    override fun deliver(context: OrderContext) = throw IllegalStateException("Pedido ja foi entregue")
    override fun cancel(context: OrderContext, reason: String) = throw IllegalStateException("Nao e possivel cancelar um pedido que ja foi entregue ao cliente")
}

class CancelledState(val reason: String) : OrderState {
    override fun getName(): String = "CANCELLED"

    override fun pay(context: OrderContext) = throw IllegalStateException("Pedido cancelado nao pode ser pago")
    override fun ship(context: OrderContext) = throw IllegalStateException("Pedido cancelado nao pode ser enviado")
    override fun deliver(context: OrderContext) = throw IllegalStateException("Pedido cancelado nao pode ser entregue")
    override fun cancel(context: OrderContext, reason: String) = throw IllegalStateException("Pedido ja esta cancelado")
}
