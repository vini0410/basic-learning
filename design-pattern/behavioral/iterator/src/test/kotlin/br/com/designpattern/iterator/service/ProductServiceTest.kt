package br.com.designpattern.iterator.service

import br.com.designpattern.iterator.domain.Product
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductServiceTest {
    private lateinit var productService: ProductService
    private val testProducts = mutableListOf(
        Product(1, "Laptop", 2999.99),
        Product(2, "Smartphone", 1499.99),
        Product(3, "Headphones", 399.99),
        Product(4, "Keyboard", 99.99),
        Product(5, "Mouse", 59.99)
    )

    @BeforeEach
    fun setup() {
        productService = ProductService()
        testProducts.forEach { productService.addProduct(it) }
    }

    @Test
    fun `should add product to service`() {
        val newProduct = Product(6, "Monitor", 499.99)
        assertEquals(5, productService.getProductsIterator(10).countAllItems())

        productService.addProduct(newProduct)
        assertEquals(6, productService.getProductsIterator(10).countAllItems())
    }

    @Test
    fun `should create iterator with default batch size`() {
        // Verify by checking the first batch contains expected number of items
        val iterator = productService.getProductsIterator()
        val firstBatch = mutableListOf<Product>()
        while (iterator.hasNext()) {
            firstBatch.add(iterator.next())
        }
        assertEquals(10, firstBatch.size)
    }

    @Test
    fun `should create iterator with custom batch size`() {
        // Verify by checking the first batch contains expected number of items
        val iterator = productService.getProductsIterator(3)
        val firstBatch = mutableListOf<Product>()
        while (iterator.hasNext()) {
            firstBatch.add(iterator.next())
        }
        assertEquals(3, firstBatch.size)
    }

    @Test
    fun `iterator should have next when products remain`() {
        val iterator = productService.getProductsIterator(2)

        // Should have next for first batch
        assertTrue(iterator.hasNext())
        iterator.next()

        // Should still have next after consuming one batch
        assertTrue(iterator.hasNext())
    }

    @Test
    fun `iterator should not have next when all products consumed`() {
        val iterator = productService.getProductsIterator(10)

        // Consume all products
        while (iterator.hasNext()) {
            iterator.next()
        }

        assertFalse(iterator.hasNext())
    }

    @Test
    fun `iterator should return correct product in sequence`() {
        val iterator = productService.getProductsIterator(10)

        assertEquals(testProducts[0], iterator.next())
        assertEquals(testProducts[1], iterator.next())
        assertEquals(testProducts[2], iterator.next())
    }

    @Test
    fun `iterator should reset position when recreated`() {
        val iterator1 = productService.getProductsIterator(2)
        iterator1.next() // Consume first batch

        val iterator2 = productService.getProductsIterator(2)
        assertEquals(testProducts[0], iterator2.next()) // Should start from beginning
    }

    @Test
    fun `processAll should call callback for each product`() {
        var callbackCount = 0
        val processedProducts = mutableListOf<Product>()

        productService.processAllProducts(2) { product ->
            callbackCount++
            processedProducts.add(product)
        }

        assertEquals(5, callbackCount)
        assertEquals(testProducts, processedProducts)
    }

    @Test
    fun `processAll should work with different batch sizes`() {
        val batchSizes = listOf(1, 2, 3, 5, 10)

        batchSizes.forEach { batchSize ->
            val processedProducts = mutableListOf<Product>()
            productService.processAllProducts(batchSize) { product ->
                processedProducts.add(product)
            }

            assertEquals(5, processedProducts.size, "Batch size $batchSize failed")
            assertEquals(testProducts, processedProducts, "Batch size $batchSize produced wrong products")
        }
    }

    @Test
    fun `processAll should handle empty product list`() {
        val emptyService = ProductService()
        var callbackCalled = false

        emptyService.processAllProducts(5) { _ ->
            callbackCalled = true
        }

        assertFalse(callbackCalled)
    }

    @Test
    fun `iterator should handle single product correctly`() {
        val singleProductService = ProductService()
        singleProductService.addProduct(Product(1, "Single", 9.99))

        val iterator = singleProductService.getProductsIterator(5)
        assertTrue(iterator.hasNext())
        assertEquals(Product(1, "Single", 9.99), iterator.next())
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `should maintain product order`() {
        val orderedService = ProductService()
        (1..10).forEach { id ->
            orderedService.addProduct(Product(id.toLong(), "Product $id", id * 10.0))
        }

        val iterator = orderedService.getProductsIterator(3)
        val results = mutableListOf<Product>()
        while (iterator.hasNext()) {
            results.add(iterator.next())
        }

        assertEquals(10, results.size)
        (0..9).forEach { i ->
            assertEquals(i + 1L, results[i].id)
            assertEquals("Product ${i + 1}", results[i].name)
            assertEquals((i + 1) * 10.0, results[i].price)
        }
    }

    @Test
    fun `iterator should be thread-safe for read operations`() {
        val iterator = productService.getProductsIterator(5)

        // Multiple threads reading should get consistent results
        val threadResults = mutableListOf<List<Product>>()
        val threads = (1..3).map {
            Thread {
                val batch = mutableListOf<Product>()
                while (iterator.hasNext()) {
                    batch.add(iterator.next())
                }
                threadResults.add(batch)
            }
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        // All threads should see the same products
        assertEquals(testProducts, threadResults[0])
    }
}
