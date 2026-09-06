package com.study.VisitorApp.service

import com.study.VisitorApp.domain.visitor.DigitalProduct
import com.study.VisitorApp.domain.visitor.PhysicalProduct
import com.study.VisitorApp.domain.visitor.ProductCategory
import com.study.VisitorApp.domain.visitor.StockReportVisitor
import com.study.VisitorApp.domain.visitor.TaxCalculationVisitor
import com.study.VisitorApp.dto.SummaryReportResponse
import com.study.VisitorApp.dto.TaxReportResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CatalogReportService {

    // Arvore de catalogo simulada em memoria
    private val sampleCatalog = ProductCategory(
        name = "Eletronicos",
        elements = listOf(
            PhysicalProduct("P-1", "Notebook Gamer", 5000.0, 2.5),
            PhysicalProduct("P-2", "Smartphone 5G", 3000.0, 0.4),
            ProductCategory(
                name = "Software & Jogos",
                elements = listOf(
                    DigitalProduct("D-1", "Licenca Antivirus 1 Ano", 100.0, 50.0),
                    DigitalProduct("D-2", "Jogo AAA Digital", 250.0, 75000.0)
                )
            )
        )
    )

    fun calculateTaxReport(): TaxReportResponse {
        val taxVisitor = TaxCalculationVisitor()
        sampleCatalog.accept(taxVisitor)

        logger.info { "Visitor de imposto executado. Total Impostos: R$ ${taxVisitor.totalTax}" }

        return TaxReportResponse(
            totalTax = taxVisitor.totalTax,
            totalPhysicalTax = taxVisitor.totalPhysicalTax,
            totalDigitalTax = taxVisitor.totalDigitalTax
        )
    }

    fun generateSummaryReport(): SummaryReportResponse {
        val stockVisitor = StockReportVisitor()
        sampleCatalog.accept(stockVisitor)

        logger.info { "Visitor de relatorio executado. Produtos Físicos: ${stockVisitor.physicalCount}, Digitais: ${stockVisitor.digitalCount}" }

        return SummaryReportResponse(
            physicalCount = stockVisitor.physicalCount,
            digitalCount = stockVisitor.digitalCount,
            categoryCount = stockVisitor.categoryCount,
            totalCatalogPrice = stockVisitor.totalCatalogPrice
        )
    }
}
