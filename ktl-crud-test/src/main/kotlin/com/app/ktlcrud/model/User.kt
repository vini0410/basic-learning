package com.app.ktlcrud.model


//@Document(collection = "users")
data class User(

    val id: String?,

    val name: String,

    val surname: String,

    val nickname: String,

    val password: String, // Note: In a real app, this should be encrypted

    val addresses: List<Address> = emptyList()
)
