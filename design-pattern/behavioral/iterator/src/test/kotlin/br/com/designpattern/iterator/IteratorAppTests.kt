package br.com.designpattern.iterator

import br.com.designpattern.iterator.domain.Product
import br.com.designpattern.iterator.service.ProductService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IteratorAppTests {
    @Autowired
    lateinit var productService: ProductService

    @BeforeEach
    fun setup() {
        // Clear products before each test
        val field = ProductService::class.java.getDeclaredField("products")
        field.isAccessible = true
        (field.get(productService) as MutableList<Product>).clear()
    }

    @Test
    fun `should iterate through products in batches`() {
        // Arrange
        repeat(25) { i -> 
            productService.addProduct(Product(i.toLong(), "Product $i", 10.0 + i))
        }

        // Act & Assert
        var count = 0
        val iterator = productService.getProductsIterator(10)
        
        while (iterator.hasNext()) {
            val product = iterator.next()
            assertEquals("Product ${count}", product.name)
            count++
        }
        
        assertEquals(25, count)
    }

    @Test
    fun `should process all products with callback`() {
        // Arrange
        repeat(15) { i -> 
            productService.addProduct(Product(i.toLong(), "Product $i", 10.0 + i))
        }

        val processedProducts = mutableListOf<Product>()
        
        // Act
        productService.processAllProducts(5) { product -> 
            processedProducts.add(product)
        }
        
        // Assert
        assertEquals(15, processedProducts.size)
    }

    @Test
    fun `should handle empty product list`() {
        // Act & Assert
        val iterator = productService.getProductsIterator()
        assertEquals(false, iterator.hasNext())
    }
}
