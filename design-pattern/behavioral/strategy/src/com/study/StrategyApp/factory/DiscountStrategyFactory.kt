package com.study.StrategyApp.factory

import com.study.StrategyApp.domain.strategy.BlackFridayDiscountStrategy
import com.study.StrategyApp.domain.strategy.DiscountStrategy
import com.study.StrategyApp.domain.strategy.FirstPurchaseDiscountStrategy
import com.study.StrategyApp.domain.strategy.NoDiscountStrategy
import com.study.StrategyApp.domain.strategy.ProgressiveValueDiscountStrategy
import com.study.StrategyApp.domain.strategy.VipCustomerDiscountStrategy
import org.springframework.stereotype.Component

@Component
class DiscountStrategyFactory(
    private val blackFridayStrategy: BlackFridayDiscountStrategy,
    private val vipCustomerStrategy: VipCustomerDiscountStrategy,
    private val firstPurchaseStrategy: FirstPurchaseDiscountStrategy,
    private val progressiveStrategy: ProgressiveValueDiscountStrategy,
    private val noDiscountStrategy: NoDiscountStrategy
) {

    fun getStrategy(couponOrType: String?): DiscountStrategy {
        if (couponOrType.isNullOrBlank()) return noDiscountStrategy

        return when (couponOrType.trim().uppercase()) {
            "BLACKFRIDAY", "BF30" -> blackFridayStrategy
            "VIP", "VIP20" -> vipCustomerStrategy
            "PRIMEIRA_COMPRA", "FIRST10" -> firstPurchaseStrategy
            "PROGRESSIVO" -> progressiveStrategy
            else -> noDiscountStrategy
        }
    }
}
