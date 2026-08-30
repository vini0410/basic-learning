package br.com.designpattern.chainofresponsibility.service

import org.springframework.stereotype.Service

@Service
class OrderValidationService(
    private val balanceCheckHandler: BalanceCheckHandler,
    private val stockCheckHandler: StockCheckHandler,
    private val fraudCheckHandler: FraudCheckHandler,
    private val addressCheckHandler: AddressCheckHandler
) {
    fun validateOrder(order: Order): ValidationResult {
        // Configura a cadeia de responsabilidades
        balanceCheckHandler.setNext(stockCheckHandler)
            .setNext(fraudCheckHandler)
            .setNext(addressCheckHandler)

        return balanceCheckHandler.handle(order)
    }
}
