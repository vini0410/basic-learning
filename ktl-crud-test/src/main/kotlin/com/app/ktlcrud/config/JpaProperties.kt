package com.app.ktlcrud.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.jpa")
data class JpaProperties(
    var showSql: Boolean = false,
    var hibernate: HibernateProps = HibernateProps(),
    var properties: JpaVendorProperties = JpaVendorProperties()
) {
    data class HibernateProps(
        var ddlAuto: String = "none"
    )

    data class JpaVendorProperties(
        var hibernate: HibernateVendorProperties = HibernateVendorProperties()
    )

    data class HibernateVendorProperties(
        var dialect: String = "",
        var format_sql: Boolean = false
    )
}