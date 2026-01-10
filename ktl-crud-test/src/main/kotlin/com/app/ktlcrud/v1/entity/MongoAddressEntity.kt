package com.app.ktlcrud.v1.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "addresses")
data class MongoAddressEntity(
    @Id
    val id: String? = null,
    val userId: String, // Link to MongoUserEntity
    val code: String,
    val street: String,
    val number: String,
    val complement: String?
)
