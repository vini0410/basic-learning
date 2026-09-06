package com.study.NullObjectApp.domain.user

/**
 * Interface do Padrão Null Object.
 * Define o contrato compartilhado entre o objeto real e o objeto nulo.
 */
interface User {
    fun getId(): String
    fun getName(): String
    fun getEmail(): String
    fun getRole(): String
    fun getDiscountRate(): Double
    fun hasAccess(permission: String): Boolean
    fun isNull(): Boolean
}

class RegisteredUser(
    private val id: String,
    private val name: String,
    private val email: String,
    private val role: String,
    private val discountRate: Double,
    private val permissions: Set<String>
) : User {
    override fun getId(): String = id
    override fun getName(): String = name
    override fun getEmail(): String = email
    override fun getRole(): String = role
    override fun getDiscountRate(): Double = discountRate
    override fun hasAccess(permission: String): Boolean = permissions.contains(permission.uppercase())
    override fun isNull(): Boolean = false
}

/**
 * Null Object: Representa um usuario anonimo (Guest) com comportamento seguro "do-nothing" ou valores padrao neutros.
 * Elimina a necessidade de verificacoes if (user != null) no codigo cliente.
 */
class NullUser : User {
    override fun getId(): String = "GUEST-000"
    override fun getName(): String = "Visitante Anônimo"
    override fun getEmail(): String = "guest@system.local"
    override fun getRole(): String = "GUEST"
    override fun getDiscountRate(): Double = 0.0
    override fun hasAccess(permission: String): Boolean = false
    override fun isNull(): Boolean = true
}
