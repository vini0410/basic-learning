package br.com.designpattern.iterator.controller

import br.com.designpattern.iterator.domain.Product
import br.com.designpattern.iterator.service.ProductService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest(@Autowired val mockMvc: MockMvc, @Autowired val productService: ProductService) {

    @BeforeEach
    fun setup() {
        // Clear products before each test
        val field = ProductService::class.java.getDeclaredField("products")
        field.isAccessible = true
        (field.get(productService) as MutableList<Product>).clear()
    }

    @Test
    fun `should add product successfully`() {
        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Product 1\",\"price\":9.99}")
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Product added successfully"))
    }

    @Test
    fun `should iterate products in batches`() {
        // Add test data
        repeat(15) { i ->
            productService.addProduct(Product(i.toLong(), "Product $i", 10.0 + i))
        }

        mockMvc.perform(
            get("/api/products/iterate")
                .param("batchSize", "5")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalProcessed").value(15))
    }

    @Test
    fun `should process all products with callback`() {
        // Add test data
        repeat(20) { i ->
            productService.addProduct(Product(i.toLong(), "Product $i", 10.0 + i))
        }

        mockMvc.perform(
            get("/api/products/iterate")
                .param("batchSize", "7")
                .param("processAll", "true")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.processedProducts.length()").value(20))
    }
}
