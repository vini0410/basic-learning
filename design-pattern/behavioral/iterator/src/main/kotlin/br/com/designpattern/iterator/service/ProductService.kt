package br.com.designpattern.iterator.service

import br.com.designpattern.iterator.domain.Product
import br.com.designpattern.iterator.domain.ProductListIterator
import org.springframework.stereotype.Component

@Component
class ProductService {
    private val products = mutableListOf<Product>()

    fun addProduct(product: Product) {
        products.add(product)
    }

    fun getProductsIterator(batchSize: Int = 10): ProductListIterator {
        return ProductListIterator(products, batchSize)
    }

    fun processAllProducts(batchSize: Int = 10, onProduct: (Product) -> Unit) {
        val iterator = getProductsIterator(batchSize)
        while (iterator.hasNext()) {
            val product = iterator.next()
            onProduct(product)
        }
    }
}
