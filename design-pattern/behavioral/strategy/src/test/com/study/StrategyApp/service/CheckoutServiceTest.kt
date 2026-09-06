package com.study.StrategyApp.service

import com.study.StrategyApp.domain.strategy.BlackFridayDiscountStrategy
import com.study.StrategyApp.dto.CalculateDiscountRequest
import com.study.StrategyApp.factory.DiscountStrategyFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CheckoutServiceTest {

    private lateinit var factory: DiscountStrategyFactory
    private lateinit var service: CheckoutService

    @BeforeEach
    fun setUp() {
        factory = mockk()
        service = CheckoutService(factory)
    }

    @Test
    fun `deve calcular checkout aplicando a estrategia retornada pela factory (happy path)`() {
        val request = CalculateDiscountRequest(amount = 200.0, couponOrType = "BLACKFRIDAY")
        every { factory.getStrategy("BLACKFRIDAY") } returns BlackFridayDiscountStrategy()

        val response = service.calculateCheckout(request)

        assertEquals(200.0, response.originalAmount)
        assertEquals(60.0, response.discountApplied)
        assertEquals(140.0, response.finalAmount)
        assertEquals("BLACK_FRIDAY_30_PERCENT", response.strategyApplied)
    }

    @Test
    fun `deve lancar IllegalArgumentException se valor da compra for negativo (edge case)`() {
        val request = CalculateDiscountRequest(amount = -50.0)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.calculateCheckout(request)
        }
        assertEquals("Valor da compra nao pode ser negativo", exception.message)
    }
}
