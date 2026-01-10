package com.app.ktlcrud.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "app.datasource.postgres")
data class PostgresDataSourceProperties(
    var url: String = "",
    var username: String = "",
    var password: String = "",
    var driverClassName: String = "org.postgresql.Driver"
)