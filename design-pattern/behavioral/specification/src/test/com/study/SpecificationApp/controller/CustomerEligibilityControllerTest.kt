package com.study.SpecificationApp.controller

import com.study.SpecificationApp.dto.EligibilityCheckResponse
import com.study.SpecificationApp.service.CustomerEligibilityService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CustomerEligibilityController::class)
class CustomerEligibilityControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var eligibilityService: CustomerEligibilityService

    @TestConfiguration
    class TestConfig {
        @Bean
        fun customerEligibilityService(): CustomerEligibilityService = mockk()
    }

    @Test
    fun `GET api customers id check-loan deve retornar 200 OK com resultado (happy path)`() {
        val response = EligibilityCheckResponse("C-100", "Ana Silva", "LOAN_RULE", true)
        every { eligibilityService.checkLoanEligibility("C-100") } returns response

        mockMvc.perform(get("/api/customers/C-100/check-loan"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isEligible").value(true))
            .andExpect(jsonPath("$.customerId").value("C-100"))
    }
}
