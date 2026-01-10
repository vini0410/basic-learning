package com.app.ktlcrud.v1.entity.mapper

import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import com.app.ktlcrud.v1.entity.MongoAddressEntity
import com.app.ktlcrud.v1.entity.MongoUserEntity

fun User.toMongoEntity(): MongoUserEntity {
    return MongoUserEntity(
        id = this.id,
        name = this.name,
        surname = this.surname,
        nickname = this.nickname,
        password = this.password,
        addressIds = this.addresses.mapNotNull { it.id } // Collect IDs from addresses
    )
}

fun Address.toMongoEntity(userId: String): MongoAddressEntity {
    return MongoAddressEntity(
        id = this.id,
        userId = userId,
        code = this.code,
        street = this.street,
        number = this.number,
        complement = this.complement
    )
}

fun MongoUserEntity.toModel(): User {
    return User(
        id = this.id,
        name = this.name,
        surname = this.surname,
        nickname = this.nickname,
        password = this.password,
        addresses = emptyList() // Will be populated by the service explicitly when needed
    )
}

fun MongoAddressEntity.toModel(): Address {
    return Address(
        id = this.id,
        userId = this.userId,
        code = this.code,
        street = this.street,
        number = this.number,
        complement = this.complement
    )
}
