package com.study.VisitorApp.domain.visitor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VisitorPatternTest {

    @Test
    fun `TaxCalculationVisitor deve calcular impostos corretos para produtos fisicos e digitais (happy path)`() {
        val rootCategory = ProductCategory(
            name = "Loja",
            elements = listOf(
                PhysicalProduct("P1", "Teclado", 100.0, 1.0), // ICMS 18 + Frete (1 * 2) = 20.0
                DigitalProduct("D1", "Ebook", 50.0, 10.0)      // ISS 5% (50 * 0.05) = 2.5
            )
        )

        val taxVisitor = TaxCalculationVisitor()
        rootCategory.accept(taxVisitor)

        assertEquals(20.0, taxVisitor.totalPhysicalTax)
        assertEquals(2.5, taxVisitor.totalDigitalTax)
        assertEquals(22.5, taxVisitor.totalTax)
    }

    @Test
    fun `StockReportVisitor deve contar elementos e totalizar preco na arvore (happy path)`() {
        val subCategory = ProductCategory(
            name = "Sub",
            elements = listOf(DigitalProduct("D1", "Curso", 200.0, 500.0))
        )
        val rootCategory = ProductCategory(
            name = "Root",
            elements = listOf(
                PhysicalProduct("P1", "Mouse", 100.0, 0.2),
                subCategory
            )
        )

        val stockVisitor = StockReportVisitor()
        rootCategory.accept(stockVisitor)

        assertEquals(1, stockVisitor.physicalCount)
        assertEquals(1, stockVisitor.digitalCount)
        assertEquals(2, stockVisitor.categoryCount) // Root + Sub
        assertEquals(300.0, stockVisitor.totalCatalogPrice)
    }
}
