package com.study.StrategyApp.domain.strategy

import org.springframework.stereotype.Component

/**
 * Interface do Padrão Strategy.
 * Define a assinatura comum para todos os algoritmos de desconto intercambiaveis.
 */
interface DiscountStrategy {
    fun getStrategyName(): String
    fun calculateDiscount(amount: Double): Double
}

@Component("blackFridayStrategy")
class BlackFridayDiscountStrategy : DiscountStrategy {
    override fun getStrategyName(): String = "BLACK_FRIDAY_30_PERCENT"

    override fun calculateDiscount(amount: Double): Double {
        return amount * 0.30
    }
}

@Component("vipCustomerStrategy")
class VipCustomerDiscountStrategy : DiscountStrategy {
    override fun getStrategyName(): String = "VIP_CUSTOMER_20_PERCENT"

    override fun calculateDiscount(amount: Double): Double {
        return amount * 0.20
    }
}

@Component("firstPurchaseStrategy")
class FirstPurchaseDiscountStrategy : DiscountStrategy {
    override fun getStrategyName(): String = "FIRST_PURCHASE_10_PERCENT_PLUS_15"

    override fun calculateDiscount(amount: Double): Double {
        val percentageDiscount = amount * 0.10
        val fixedBonus = 15.0
        val totalDiscount = percentageDiscount + fixedBonus
        return totalDiscount.coerceAtMost(amount) // Desconto nao pode exceder o valor total
    }
}

@Component("progressiveStrategy")
class ProgressiveValueDiscountStrategy : DiscountStrategy {
    override fun getStrategyName(): String = "PROGRESSIVE_VALUE"

    override fun calculateDiscount(amount: Double): Double {
        return when {
            amount >= 500.0 -> amount * 0.15
            amount >= 200.0 -> amount * 0.10
            amount >= 100.0 -> amount * 0.05
            else -> 0.0
        }
    }
}

@Component("noDiscountStrategy")
class NoDiscountStrategy : DiscountStrategy {
    override fun getStrategyName(): String = "NO_DISCOUNT"

    override fun calculateDiscount(amount: Double): Double {
        return 0.0
    }
}
