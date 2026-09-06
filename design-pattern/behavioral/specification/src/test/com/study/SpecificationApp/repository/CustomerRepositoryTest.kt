package com.study.SpecificationApp.repository

import com.study.SpecificationApp.domain.customer.HasPremiumPlanSpecification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CustomerRepositoryTest {

    private lateinit var repository: CustomerRepository

    @BeforeEach
    fun setUp() {
        repository = CustomerRepository()
    }

    @Test
    fun `deve buscar cliente por id existente (happy path)`() {
        val customer = repository.findById("C-100")
        assertEquals("Ana Silva", customer?.name)
    }

    @Test
    fun `deve filtrar clientes por Specification no repositorio (happy path)`() {
        val premiumSpec = HasPremiumPlanSpecification()
        val premiumCustomers = repository.findAllMatching(premiumSpec)

        assertEquals(2, premiumCustomers.size)
    }
}
