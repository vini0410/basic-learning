package com.app.ktlcrud.controller.dto

data class AddressDTO(
    val code: String,
    val street: String,
    val number: String,
    val complement: String?
)
