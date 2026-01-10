package com.app.ktlcrud.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.Primary
import org.springframework.boot.context.properties.EnableConfigurationProperties
import java.util.*

@Configuration
@Profile("!test-context")
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["com.app.KtlCrudTest.v2.repository"],
    entityManagerFactoryRef = "postgresEntityManagerFactory",
    transactionManagerRef = "postgresTransactionManager"
)
@EnableConfigurationProperties(PostgresDataSourceProperties::class, JpaProperties::class)
class PostgresConfig(
    private val postgresDataSourceProperties: PostgresDataSourceProperties,
    private val jpaProperties: JpaProperties
) {

    @Bean
    @Primary
    fun postgresDataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(postgresDataSourceProperties.driverClassName)
        dataSource.url = postgresDataSourceProperties.url
        dataSource.username = postgresDataSourceProperties.username
        dataSource.password = postgresDataSourceProperties.password
        return dataSource
    }

    @Bean
    @Primary
    fun postgresEntityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = postgresDataSource()
        em.setPackagesToScan("com.app.KtlCrudTest.v2.entity")

        val vendorAdapter = HibernateJpaVendorAdapter()
        em.jpaVendorAdapter = vendorAdapter
        val properties = Properties()
        properties["hibernate.hbm2ddl.auto"] = jpaProperties.hibernate.ddlAuto
        properties["hibernate.dialect"] = jpaProperties.properties.hibernate.dialect
        properties["hibernate.show_sql"] = jpaProperties.showSql
        properties["hibernate.format_sql"] = jpaProperties.properties.hibernate.format_sql
        em.setJpaProperties(properties)
        return em
    }

    @Bean
    @Primary
    fun postgresTransactionManager(): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = postgresEntityManagerFactory().`object`
        return transactionManager
    }
}
