package com.study.SpecificationApp.service

import com.study.SpecificationApp.domain.customer.HasMinimumCreditScoreSpecification
import com.study.SpecificationApp.domain.customer.HasNoOverdueDebtSpecification
import com.study.SpecificationApp.domain.customer.HasPremiumPlanSpecification
import com.study.SpecificationApp.domain.customer.IsActiveCustomerSpecification
import com.study.SpecificationApp.dto.EligibilityCheckResponse
import com.study.SpecificationApp.repository.CustomerRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CustomerEligibilityService(
    private val customerRepository: CustomerRepository
) {

    // Regra 1: Empréstimo = Ativo AND Sem Dívidas AND Score >= 700
    private val loanEligibilityRule = IsActiveCustomerSpecification()
        .and(HasNoOverdueDebtSpecification())
        .and(HasMinimumCreditScoreSpecification(700))

    // Regra 2: Oferta VIP = Ativo AND (Premium OR Score >= 800)
    private val vipOfferRule = IsActiveCustomerSpecification()
        .and(HasPremiumPlanSpecification().or(HasMinimumCreditScoreSpecification(800)))

    fun checkLoanEligibility(customerId: String): EligibilityCheckResponse {
        val customer = customerRepository.findById(customerId)
            ?: throw NoSuchElementException("Cliente '$customerId' nao encontrado")

        val isEligible = loanEligibilityRule.isSatisfiedBy(customer)
        logger.info { "Elegibilidade de emprestimo para cliente '$customerId': $isEligible" }

        return EligibilityCheckResponse(
            customerId = customer.id,
            customerName = customer.name,
            ruleName = "LOAN_ELIGIBILITY_ACTIVE_AND_NO_DEBT_AND_SCORE_700",
            isEligible = isEligible
        )
    }

    fun checkVipOfferEligibility(customerId: String): EligibilityCheckResponse {
        val customer = customerRepository.findById(customerId)
            ?: throw NoSuchElementException("Cliente '$customerId' nao encontrado")

        val isEligible = vipOfferRule.isSatisfiedBy(customer)
        logger.info { "Elegibilidade de oferta VIP para cliente '$customerId': $isEligible" }

        return EligibilityCheckResponse(
            customerId = customer.id,
            customerName = customer.name,
            ruleName = "VIP_OFFER_ACTIVE_AND_PREMIUM_OR_SCORE_800",
            isEligible = isEligible
        )
    }
}
