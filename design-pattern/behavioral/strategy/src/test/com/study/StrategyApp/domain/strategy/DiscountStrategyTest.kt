package com.study.StrategyApp.domain.strategy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiscountStrategyTest {

    @Test
    fun `BlackFridayDiscountStrategy deve aplicar 30 por cento de desconto (happy path)`() {
        val strategy = BlackFridayDiscountStrategy()
        val discount = strategy.calculateDiscount(1000.0)
        assertEquals(300.0, discount)
    }

    @Test
    fun `VipCustomerDiscountStrategy deve aplicar 20 por cento de desconto (happy path)`() {
        val strategy = VipCustomerDiscountStrategy()
        val discount = strategy.calculateDiscount(500.0)
        assertEquals(100.0, discount)
    }

    @Test
    fun `FirstPurchaseDiscountStrategy deve aplicar 10 por cento mais 15 de bonus (happy path)`() {
        val strategy = FirstPurchaseDiscountStrategy()
        val discount = strategy.calculateDiscount(100.0)
        assertEquals(25.0, discount) // 10% de 100 = 10 + 15 = 25
    }

    @Test
    fun `FirstPurchaseDiscountStrategy nao deve retornar desconto maior que o valor total (edge case)`() {
        val strategy = FirstPurchaseDiscountStrategy()
        val discount = strategy.calculateDiscount(10.0)
        assertEquals(10.0, discount) // 10% de 10 = 1 + 15 = 16 -> limitado a 10.0
    }

    @Test
    fun `ProgressiveValueDiscountStrategy deve variar desconto conforme o valor total (happy path)`() {
        val strategy = ProgressiveValueDiscountStrategy()

        assertEquals(90.0, strategy.calculateDiscount(600.0)) // 15% de 600
        assertEquals(30.0, strategy.calculateDiscount(300.0)) // 10% de 300
        assertEquals(7.5, strategy.calculateDiscount(150.0))  // 5% de 150
        assertEquals(0.0, strategy.calculateDiscount(50.0))   // 0% de 50
    }

    @Test
    fun `NoDiscountStrategy deve retornar 0 de desconto (happy path)`() {
        val strategy = NoDiscountStrategy()
        assertEquals(0.0, strategy.calculateDiscount(1000.0))
    }
}
