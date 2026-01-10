package com.app.ktlcrud.v2.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    val id: String? = null, // Default null for new entities

    @Column(name = "name")
    var name: String,

    @Column(name = "surname")
    var surname: String,

    @Column(name = "nickname")
    var nickname: String,

    @Column(name = "password")
    var password: String // Note: In a real app, this should be encrypted
)

