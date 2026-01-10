package com.app.ktlcrud.config
import org.springframework.context.annotation.Profile
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@Profile("!test-context")
@EnableMongoRepositories(basePackages = ["com.app.KtlCrudTest.v1.repository"], mongoTemplateRef = "mongoTemplate")
class MongoConfig : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        return "KtlCrudDB"
    }

    @Bean(name = ["mongoClient"])
    override fun mongoClient(): MongoClient {
        val connectionString =
            ConnectionString("mongodb+srv://CrudTestUser:6gAGbDkwNoWcPRnQ@crudtest.xed1svd.mongodb.net/KtlCrudTestDB?appName=CrudTest")
        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientSettings)
    }
}
