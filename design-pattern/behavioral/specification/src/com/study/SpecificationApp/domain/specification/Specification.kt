package com.study.SpecificationApp.domain.specification

/**
 * Interface funcional do Padrão Specification.
 */
interface Specification<T> {
    fun isSatisfiedBy(item: T): Boolean

    fun and(other: Specification<T>): Specification<T> = AndSpecification(this, other)
    fun or(other: Specification<T>): Specification<T> = OrSpecification(this, other)
    fun not(): Specification<T> = NotSpecification(this)
}

class AndSpecification<T>(
    private val spec1: Specification<T>,
    private val spec2: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(item: T): Boolean {
        return spec1.isSatisfiedBy(item) && spec2.isSatisfiedBy(item)
    }
}

class OrSpecification<T>(
    private val spec1: Specification<T>,
    private val spec2: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(item: T): Boolean {
        return spec1.isSatisfiedBy(item) || spec2.isSatisfiedBy(item)
    }
}

class NotSpecification<T>(
    private val spec: Specification<T>
) : Specification<T> {
    override fun isSatisfiedBy(item: T): Boolean {
        return !spec.isSatisfiedBy(item)
    }
}
