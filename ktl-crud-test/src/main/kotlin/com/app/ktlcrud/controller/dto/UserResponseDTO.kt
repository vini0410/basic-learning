package com.app.ktlcrud.controller.dto

data class UserResponseDTO(
    val id: String?,
    val name: String,
    val surname: String,
    val nickname: String,
    val addresses: List<AddressDTO>
)
