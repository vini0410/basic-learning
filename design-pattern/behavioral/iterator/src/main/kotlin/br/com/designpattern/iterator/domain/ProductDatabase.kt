package br.com.designpattern.iterator.domain

class ProductDatabase(private val products: List<Product>) : Aggregate<Product> {
    override fun createIterator(): Iterator<Product> {
        return ProductListIterator(products)
    }
}
