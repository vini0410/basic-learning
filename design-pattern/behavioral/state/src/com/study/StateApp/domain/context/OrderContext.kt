package com.study.StateApp.domain.context

import com.study.StateApp.domain.state.OrderState
import com.study.StateApp.domain.state.PendingPaymentState

/**
 * Contexto do State Pattern.
 * Delega os comportamentos de transicao de estado para a classe de estado atual.
 */
class OrderContext(
    val id: String,
    val customerId: String,
    val totalAmount: Double,
    initialState: OrderState = PendingPaymentState()
) {
    var currentState: OrderState = initialState
        private set

    fun changeState(newState: OrderState) {
        this.currentState = newState
    }

    fun pay() {
        currentState.pay(this)
    }

    fun ship() {
        currentState.ship(this)
    }

    fun deliver() {
        currentState.deliver(this)
    }

    fun cancel(reason: String) {
        currentState.cancel(this, reason)
    }
}
