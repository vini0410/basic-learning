package com.app.ktlcrud.model

//@Document(collection = "addresses")
data class Address(
    val id: String?,

    val userId: String,

    val code: String,

    val street: String,

    val number: String,

    val complement: String?
)
