package com.app.ktlcrud.v2.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "addresses")
data class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    val id: String? = null,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "code")
    val code: String,

    @Column(name = "street")
    val street: String,

    @Column(name = "number")
    val number: String,

    @Column(name = "complement")
    val complement: String?
)

