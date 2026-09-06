package com.study.NullObjectApp.dto

import com.study.NullObjectApp.domain.user.User
import java.time.LocalDateTime

data class UserProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val discountRate: Double,
    val isAnonymous: Boolean
) {
    companion object {
        fun fromDomain(user: User): UserProfileDto {
            return UserProfileDto(
                id = user.getId(),
                name = user.getName(),
                email = user.getEmail(),
                role = user.getRole(),
                discountRate = user.getDiscountRate(),
                isAnonymous = user.isNull()
            )
        }
    }
}

data class CheckAccessRequest(
    val userId: String?,
    val permission: String
)

data class AccessCheckResponse(
    val userId: String,
    val permission: String,
    val hasAccess: Boolean
)

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
