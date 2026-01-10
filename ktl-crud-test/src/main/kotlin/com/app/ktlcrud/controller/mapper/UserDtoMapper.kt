package com.app.ktlcrud.controller.mapper

import com.app.ktlcrud.controller.dto.AddressDTO
import com.app.ktlcrud.controller.dto.UserRequestDTO
import com.app.ktlcrud.controller.dto.UserResponseDTO
import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User

fun UserRequestDTO.toModel(): User {
    return User(
        id = null,
        name = this.name,
        surname = this.surname,
        nickname = this.nickname,
        password = this.password,
        addresses = this.addresses.map { it.toModel() }
    )
}

fun AddressDTO.toModel(): Address {
    return Address(
        userId = "", // Will be populated by the service
        code = this.code,
        street = this.street,
        number = this.number,
        complement = this.complement,
        id = null
    )
}

fun User.toResponseDTO(): UserResponseDTO {
    return UserResponseDTO(
        id = this.id,
        name = this.name,
        surname = this.surname,
        nickname = this.nickname,
        addresses = this.addresses.map { it.toDTO() }
    )
}

fun Address.toDTO(): AddressDTO {
    return AddressDTO(
        code = this.code,
        street = this.street,
        number = this.number,
        complement = this.complement
    )
}