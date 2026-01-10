package com.app.ktlcrud.v2.entity.mapper

import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import com.app.ktlcrud.v2.entity.AddressEntity
import com.app.ktlcrud.v2.entity.UserEntity

fun User.toEntity(): UserEntity {
    return UserEntity(
        name = this.name,
        surname = this.surname,
        nickname = this.nickname,
        password = this.password,
        id = this.id // Keep existing ID if present for updates
    )
}

// Updated mapping for Address to AddressEntity
fun Address.toEntity(userId: String?): AddressEntity { // userId is now explicit
    return AddressEntity(
        id = this.id,
        userId = userId ?: "", // Use the passed userId, or empty string if null (shouldn't be null for associated addresses)
        code = this.code,
        street = this.street,
        number = this.number,
        complement = this.complement
    )
}

fun UserEntity.toModel(): User {
    return User(
        id = this.id,
        name = this.name,
        surname = this.surname,
        nickname = this.nickname,
        password = this.password, // In a real app, hash this password
        addresses = emptyList() // Addresses will be populated by the service explicitly when needed
    )
}

// Updated mapping for AddressEntity to Address model
fun AddressEntity.toModel(): Address {
    return Address(
        id = this.id,
        userId = this.userId,
        code = this.code,
        street = this.street,
        number = this.number,
        complement = this.complement
    )
}
