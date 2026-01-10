package com.app.ktlcrud.controller.dto

data class UserRequestDTO(
    val name: String,
    val surname: String,
    val nickname: String,
    val password: String,
    val addresses: List<AddressDTO>
)
