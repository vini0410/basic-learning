package br.com.designpattern.chainofresponsibility.domain.handler

abstract class OrderHandler {
    private var nextHandler: OrderHandler? = null

    fun setNext(handler: OrderHandler): OrderHandler {
        this.nextHandler = handler
        return handler
    }

    abstract fun handle(order: Order): ValidationResult

    protected fun chain(order: Order): ValidationResult {
        return nextHandler?.handle(order) ?: ValidationResult(true, "Validation completed")
    }
}
