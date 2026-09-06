package com.study.SpecificationApp.service

import com.study.SpecificationApp.domain.customer.Customer
import com.study.SpecificationApp.repository.CustomerRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CustomerEligibilityServiceTest {

    private lateinit var repository: CustomerRepository
    private lateinit var service: CustomerEligibilityService

    @BeforeEach
    fun setUp() {
        repository = mockk()
        service = CustomerEligibilityService(repository)
    }

    @Test
    fun `deve aprovar emprestimo para cliente ativo, sem dividas e score alto (happy path)`() {
        val eligibleCustomer = Customer("C-1", "Ana", active = true, premiumPlan = false, overdueDebtAmount = 0.0, creditScore = 750)
        every { repository.findById("C-1") } returns eligibleCustomer

        val result = service.checkLoanEligibility("C-1")

        assertTrue(result.isEligible)
    }

    @Test
    fun `deve reprovar emprestimo para cliente com dividas pendentes (edge case)`() {
        val debtorCustomer = Customer("C-2", "Bruno", active = true, premiumPlan = true, overdueDebtAmount = 200.0, creditScore = 800)
        every { repository.findById("C-2") } returns debtorCustomer

        val result = service.checkLoanEligibility("C-2")

        assertFalse(result.isEligible)
    }

    @Test
    fun `deve lancar NoSuchElementException para cliente nao cadastrado (edge case)`() {
        every { repository.findById("UNKNOWN") } returns null

        assertThrows(NoSuchElementException::class.java) {
            service.checkLoanEligibility("UNKNOWN")
        }
    }
}
