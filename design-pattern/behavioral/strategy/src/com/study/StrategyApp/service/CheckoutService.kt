package com.study.StrategyApp.service

import com.study.StrategyApp.dto.CalculateDiscountRequest
import com.study.StrategyApp.dto.CalculateDiscountResponse
import com.study.StrategyApp.factory.DiscountStrategyFactory
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CheckoutService(
    private val strategyFactory: DiscountStrategyFactory
) {

    fun calculateCheckout(request: CalculateDiscountRequest): CalculateDiscountResponse {
        require(request.amount >= 0) { "Valor da compra nao pode ser negativo" }

        val strategy = strategyFactory.getStrategy(request.couponOrType)
        val discount = strategy.calculateDiscount(request.amount)
        val finalAmount = (request.amount - discount).coerceAtLeast(0.0)

        logger.info { "Estrategia de desconto '${strategy.getStrategyName()}' aplicada. Valor original: R$ ${request.amount}, Desconto: R$ $discount, Final: R$ $finalAmount" }

        return CalculateDiscountResponse(
            originalAmount = request.amount,
            discountApplied = discount,
            finalAmount = finalAmount,
            strategyApplied = strategy.getStrategyName()
        )
    }
}
