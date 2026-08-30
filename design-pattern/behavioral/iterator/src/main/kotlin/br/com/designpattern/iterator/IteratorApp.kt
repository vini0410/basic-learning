package br.com.designpattern.iterator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["br.com.designpattern.iterator"])
class IteratorApp

fun main(args: Array<String>) {
    runApplication<IteratorApp>(*args)
}
