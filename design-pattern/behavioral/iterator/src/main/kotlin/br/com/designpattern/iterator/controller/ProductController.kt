package br.com.designpattern.iterator.controller

import br.com.designpattern.iterator.domain.Product
import br.com.designpattern.iterator.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @PostMapping
    fun addProduct(@RequestBody product: Product): ResponseEntity<String> {
        productService.addProduct(product)
        return ResponseEntity.ok("Product added successfully")
    }

    @GetMapping("/iterate")
    fun iterateProducts(
        @RequestParam(defaultValue = "10") batchSize: Int,
        @RequestParam(defaultValue = "false") processAll: Boolean
    ): ResponseEntity<Map<String, Any>> {
        val results = mutableMapOf<String, Any>()
        
        if (processAll) {
            val processedProducts = mutableListOf<MutableMap<String, String>>()
            productService.processAllProducts(batchSize) { product ->
                processedProducts.add(mutableMapOf(
                    "id" to product.id.toString(),
                    "name" to product.name,
                    "price" to "R$${product.price}"
                ))
            }
            results["processedProducts"] = processedProducts
        } else {
            val iterator = productService.getProductsIterator(batchSize)
            val batchResults = mutableListOf<MutableMap<String, String>>()
            
            while (iterator.hasNext()) {
                val product = iterator.next()
                batchResults.add(mutableMapOf(
                    "id" to product.id.toString(),
                    "name" to product.name,
                    "price" to "R$${product.price}"
                ))
            }
            
            results["batchSize"] = batchSize
            results["totalProcessed"] = batchResults.size
        }
        
        return ResponseEntity.ok(results)
    }
}
