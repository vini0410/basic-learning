package br.com.designpattern.iterator.domain

import java.util.function.Consumer

interface Iterator<T> {
    fun hasNext(): Boolean
    fun next(): T
}

interface Aggregate<T> {
    fun createIterator(): Iterator<T>
}
