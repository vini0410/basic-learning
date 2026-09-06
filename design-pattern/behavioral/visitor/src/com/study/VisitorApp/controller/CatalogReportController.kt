package com.study.VisitorApp.controller

import com.study.VisitorApp.dto.SummaryReportResponse
import com.study.VisitorApp.dto.TaxReportResponse
import com.study.VisitorApp.service.CatalogReportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/catalog/reports")
class CatalogReportController(
    private val reportService: CatalogReportService
) {

    @GetMapping("/tax")
    fun getTaxReport(): ResponseEntity<TaxReportResponse> {
        val report = reportService.calculateTaxReport()
        return ResponseEntity.ok(report)
    }

    @GetMapping("/summary")
    fun getSummaryReport(): ResponseEntity<SummaryReportResponse> {
        val report = reportService.generateSummaryReport()
        return ResponseEntity.ok(report)
    }
}
