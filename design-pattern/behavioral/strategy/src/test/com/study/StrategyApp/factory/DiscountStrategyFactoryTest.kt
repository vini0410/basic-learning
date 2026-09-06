package com.study.StrategyApp.factory

import com.study.StrategyApp.domain.strategy.BlackFridayDiscountStrategy
import com.study.StrategyApp.domain.strategy.FirstPurchaseDiscountStrategy
import com.study.StrategyApp.domain.strategy.NoDiscountStrategy
import com.study.StrategyApp.domain.strategy.ProgressiveValueDiscountStrategy
import com.study.StrategyApp.domain.strategy.VipCustomerDiscountStrategy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DiscountStrategyFactoryTest {

    private lateinit var factory: DiscountStrategyFactory

    @BeforeEach
    fun setUp() {
        factory = DiscountStrategyFactory(
            BlackFridayDiscountStrategy(),
            VipCustomerDiscountStrategy(),
            FirstPurchaseDiscountStrategy(),
            ProgressiveValueDiscountStrategy(),
            NoDiscountStrategy()
        )
    }

    @Test
    fun `deve retornar a estrategia correspondente ao cupom fornecido (happy path)`() {
        assertEquals("BLACK_FRIDAY_30_PERCENT", factory.getStrategy("BLACKFRIDAY").getStrategyName())
        assertEquals("VIP_CUSTOMER_20_PERCENT", factory.getStrategy("VIP").getStrategyName())
        assertEquals("FIRST_PURCHASE_10_PERCENT_PLUS_15", factory.getStrategy("PRIMEIRA_COMPRA").getStrategyName())
        assertEquals("PROGRESSIVE_VALUE", factory.getStrategy("PROGRESSIVO").getStrategyName())
    }

    @Test
    fun `deve retornar NoDiscountStrategy para cupons desconhecidos, nulos ou em branco (edge case)`() {
        assertEquals("NO_DISCOUNT", factory.getStrategy(null).getStrategyName())
        assertEquals("NO_DISCOUNT", factory.getStrategy("   ").getStrategyName())
        assertEquals("NO_DISCOUNT", factory.getStrategy("CUPOM_INVALIDO").getStrategyName())
    }
}
