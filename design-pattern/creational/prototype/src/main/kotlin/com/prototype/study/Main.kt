package com.prototype.study

import com.prototype.study.service.CampaignPrototypeFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class PrototypeApplication {
    @Bean
    open fun campaignPrototypeFactory(): CampaignPrototypeFactory {
        val factory = CampaignPrototypeFactory()
        factory.loadPrototype()
        return factory
    }
}

fun main(args: Array<String>) {
    runApplication<PrototypeApplication>(*args)
}