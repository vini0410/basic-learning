package com.study.SpecificationApp.controller

import com.study.SpecificationApp.dto.EligibilityCheckResponse
import com.study.SpecificationApp.service.CustomerEligibilityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customers")
class CustomerEligibilityController(
    private val eligibilityService: CustomerEligibilityService
) {

    @GetMapping("/{id}/check-loan")
    fun checkLoanEligibility(@PathVariable id: String): ResponseEntity<EligibilityCheckResponse> {
        val response = eligibilityService.checkLoanEligibility(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/check-vip-offer")
    fun checkVipOfferEligibility(@PathVariable id: String): ResponseEntity<EligibilityCheckResponse> {
        val response = eligibilityService.checkVipOfferEligibility(id)
        return ResponseEntity.ok(response)
    }
}
