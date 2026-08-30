package br.com.designpattern.command

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommandApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<CommandApplication>(*args)
        }
    }
}
