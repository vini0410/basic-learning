package com.study.SpecificationApp.domain.specification

import com.study.SpecificationApp.domain.customer.Customer
import com.study.SpecificationApp.domain.customer.HasMinimumCreditScoreSpecification
import com.study.SpecificationApp.domain.customer.HasNoOverdueDebtSpecification
import com.study.SpecificationApp.domain.customer.HasPremiumPlanSpecification
import com.study.SpecificationApp.domain.customer.IsActiveCustomerSpecification
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SpecificationTest {

    @Test
    fun `AndSpecification deve ser verdadeira somente quando ambos os lados forem satisfeitos (happy path)`() {
        val activeSpec = IsActiveCustomerSpecification()
        val noDebtSpec = HasNoOverdueDebtSpecification()
        val combined = activeSpec.and(noDebtSpec)

        val validCustomer = Customer("1", "Ana", active = true, premiumPlan = false, overdueDebtAmount = 0.0, creditScore = 750)
        val debtorCustomer = Customer("2", "Bruno", active = true, premiumPlan = false, overdueDebtAmount = 100.0, creditScore = 750)

        assertTrue(combined.isSatisfiedBy(validCustomer))
        assertFalse(combined.isSatisfiedBy(debtorCustomer))
    }

    @Test
    fun `OrSpecification deve ser verdadeira quando qualquer um dos lados for satisfeito (happy path)`() {
        val premiumSpec = HasPremiumPlanSpecification()
        val score800Spec = HasMinimumCreditScoreSpecification(800)
        val combined = premiumSpec.or(score800Spec)

        val premiumCustomer = Customer("1", "Ana", active = true, premiumPlan = true, overdueDebtAmount = 0.0, creditScore = 600)
        val highScoreCustomer = Customer("2", "Bruno", active = true, premiumPlan = false, overdueDebtAmount = 0.0, creditScore = 850)
        val regularCustomer = Customer("3", "Carla", active = true, premiumPlan = false, overdueDebtAmount = 0.0, creditScore = 700)

        assertTrue(combined.isSatisfiedBy(premiumCustomer))
        assertTrue(combined.isSatisfiedBy(highScoreCustomer))
        assertFalse(combined.isSatisfiedBy(regularCustomer))
    }

    @Test
    fun `NotSpecification deve inverter o resultado da especificacao original (happy path)`() {
        val activeSpec = IsActiveCustomerSpecification()
        val inactiveSpec = activeSpec.not()

        val activeCustomer = Customer("1", "Ana", active = true, premiumPlan = false, overdueDebtAmount = 0.0, creditScore = 750)
        val inactiveCustomer = Customer("2", "Bruno", active = false, premiumPlan = false, overdueDebtAmount = 0.0, creditScore = 750)

        assertFalse(inactiveSpec.isSatisfiedBy(activeCustomer))
        assertTrue(inactiveSpec.isSatisfiedBy(inactiveCustomer))
    }
}
