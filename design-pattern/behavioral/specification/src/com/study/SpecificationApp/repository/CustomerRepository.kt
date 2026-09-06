package com.study.SpecificationApp.repository

import com.study.SpecificationApp.domain.customer.Customer
import com.study.SpecificationApp.domain.specification.Specification
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CustomerRepository {

    private val customers = ConcurrentHashMap<String, Customer>(
        mapOf(
            "C-100" to Customer("C-100", "Ana Silva", active = true, premiumPlan = true, overdueDebtAmount = 0.0, creditScore = 800),
            "C-200" to RegisteredCustomer("C-200", "Bruno Santos", active = true, premiumPlan = false, overdueDebtAmount = 150.0, creditScore = 650),
            "C-300" to RegisteredCustomer("C-300", "Carla Souza", active = false, premiumPlan = true, overdueDebtAmount = 0.0, creditScore = 750)
        )
    )

    fun findById(id: String): Customer? {
        return customers[id]
    }

    /**
     * Filtra a colecao em memoria usando o Specification Pattern.
     */
    fun findAllMatching(specification: Specification<Customer>): List<Customer> {
        return customers.values.filter { specification.isSatisfiedBy(it) }
    }
}

private fun RegisteredCustomer(id: String, name: String, active: Boolean, premiumPlan: Boolean, overdueDebtAmount: Double, creditScore: Int) =
    Customer(id, name, active, premiumPlan, overdueDebtAmount, creditScore)
