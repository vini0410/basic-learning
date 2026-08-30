package br.com.designpattern.chainofresponsibility.domain

data class ValidationResult(
    val isValid: Boolean,
    val message: String
)
