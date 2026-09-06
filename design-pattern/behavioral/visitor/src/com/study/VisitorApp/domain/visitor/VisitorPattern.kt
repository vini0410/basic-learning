package com.study.VisitorApp.domain.visitor

/**
 * Interface do elemento visitavel no Visitor Pattern.
 */
interface CatalogElement {
    fun accept(visitor: CatalogVisitor)
}

data class PhysicalProduct(
    val id: String,
    val name: String,
    val price: Double,
    val weightKg: Double
) : CatalogElement {
    override fun accept(visitor: CatalogVisitor) {
        visitor.visitPhysicalProduct(this)
    }
}

data class DigitalProduct(
    val id: String,
    val name: String,
    val price: Double,
    val downloadSizeMb: Double
) : CatalogElement {
    override fun accept(visitor: CatalogVisitor) {
        visitor.visitDigitalProduct(this)
    }
}

data class ProductCategory(
    val name: String,
    val elements: List<CatalogElement> = emptyList()
) : CatalogElement {
    override fun accept(visitor: CatalogVisitor) {
        visitor.visitCategory(this)
        elements.forEach { it.accept(visitor) }
    }
}

/**
 * Interface do Visitante (Visitor Pattern).
 */
interface CatalogVisitor {
    fun visitPhysicalProduct(product: PhysicalProduct)
    fun visitDigitalProduct(product: DigitalProduct)
    fun visitCategory(category: ProductCategory)
}

class TaxCalculationVisitor : CatalogVisitor {
    var totalTax: Double = 0.0
        private set
    var totalPhysicalTax: Double = 0.0
        private set
    var totalDigitalTax: Double = 0.0
        private set

    override fun visitPhysicalProduct(product: PhysicalProduct) {
        // Imposto Físico: 18% ICMS + R$ 2.0 por Kg de frete tributável
        val tax = (product.price * 0.18) + (product.weightKg * 2.0)
        totalPhysicalTax += tax
        totalTax += tax
    }

    override fun visitDigitalProduct(product: DigitalProduct) {
        // Imposto Digital: 5% ISS (Sem frete)
        val tax = product.price * 0.05
        totalDigitalTax += tax
        totalTax += tax
    }

    override fun visitCategory(category: ProductCategory) {
        // Nenhuma taxa direta sobre a categoria container
    }
}

class StockReportVisitor : CatalogVisitor {
    var physicalCount: Int = 0
        private set
    var digitalCount: Int = 0
        private set
    var categoryCount: Int = 0
        private set
    var totalCatalogPrice: Double = 0.0
        private set

    override fun visitPhysicalProduct(product: PhysicalProduct) {
        physicalCount++
        totalCatalogPrice += product.price
    }

    override fun visitDigitalProduct(product: DigitalProduct) {
        digitalCount++
        totalCatalogPrice += product.price
    }

    override fun visitCategory(category: ProductCategory) {
        categoryCount++
    }
}
