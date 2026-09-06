package com.study.MementoApp.domain.model

import java.time.LocalDateTime

enum class UserRole {
    USER,
    ADMIN,
    VIP,
    GUEST
}

/**
 * Memento: Objeto imutavel que guarda a foto (snapshot) do estado interno do UserProfile.
 */
data class UserProfileMemento(
    val userId: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val bio: String,
    val version: Int,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
