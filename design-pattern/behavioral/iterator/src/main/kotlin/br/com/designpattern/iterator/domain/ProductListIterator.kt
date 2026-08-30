package br.com.designpattern.iterator.domain

class ProductListIterator(private val products: List<Product>, private val batchSize: Int = 10) : Iterator<Product> {
    private var currentIndex = 0
    private var loadedProducts: List<Product> = emptyList()
    private var currentBatchIndex = 0

    init {
        loadNextBatch()
    }

    private fun loadNextBatch() {
        val startIdx = currentIndex * batchSize
        val endIdx = minOf(startIdx + batchSize, products.size)
        loadedProducts = products.subList(startIdx, endIdx)
        currentIndex++
    }

    override fun hasNext(): Boolean {
        if (currentBatchIndex < loadedProducts.size) {
            return true
        }
        
        if (currentIndex * batchSize >= products.size) {
            return false
        }
        
        loadNextBatch()
        return currentBatchIndex < loadedProducts.size
    }

    override fun next(): Product {
        if (!hasNext()) {
            throw NoSuchElementException("No more elements in the iterator")
        }
        
        val product = loadedProducts[currentBatchIndex]
        currentBatchIndex++
        
        // Load next batch if we've consumed the current one
        if (currentBatchIndex >= loadedProducts.size) {
            loadNextBatch()
            currentBatchIndex = 0
        }
        
        return product
    }

    fun countAllItems(): Int {
        return loadedProducts.size
    }
}
