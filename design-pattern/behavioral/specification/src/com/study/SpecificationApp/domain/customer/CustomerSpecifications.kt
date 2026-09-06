package com.study.SpecificationApp.domain.customer

import com.study.SpecificationApp.domain.specification.Specification

data class Customer(
    val id: String,
    val name: String,
    val active: Boolean,
    val premiumPlan: Boolean,
    val overdueDebtAmount: Double,
    val creditScore: Int
)

class IsActiveCustomerSpecification : Specification<Customer> {
    override fun isSatisfiedBy(item: Customer): Boolean {
        return item.active
    }
}

class HasPremiumPlanSpecification : Specification<Customer> {
    override fun isSatisfiedBy(item: Customer): Boolean {
        return item.premiumPlan
    }
}

class HasNoOverdueDebtSpecification : Specification<Customer> {
    override fun isSatisfiedBy(item: Customer): Boolean {
        return item.overdueDebtAmount <= 0.0
    }
}

class HasMinimumCreditScoreSpecification(
    private val minScore: Int
) : Specification<Customer> {
    override fun isSatisfiedBy(item: Customer): Boolean {
        return item.creditScore >= minScore
    }
}
