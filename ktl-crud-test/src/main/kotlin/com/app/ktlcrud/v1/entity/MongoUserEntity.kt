package com.app.ktlcrud.v1.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class MongoUserEntity(
    @Id
    val id: String? = null,
    var name: String,
    var surname: String,
    var nickname: String,
    var password: String,
    var addressIds: List<String> = emptyList() // To link addresses from a separate collection
)
