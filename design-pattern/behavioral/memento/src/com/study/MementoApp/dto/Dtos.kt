package com.study.MementoApp.dto

import com.study.MementoApp.domain.model.UserProfile
import com.study.MementoApp.domain.model.UserProfileMemento
import com.study.MementoApp.domain.model.UserRole
import java.time.LocalDateTime

data class CreateProfileRequest(
    val name: String,
    val email: String,
    val role: UserRole = UserRole.USER,
    val bio: String = ""
)

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val role: UserRole,
    val bio: String
)

data class UserProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val bio: String,
    val version: Int
) {
    companion object {
        fun fromDomain(profile: UserProfile): UserProfileResponse {
            return UserProfileResponse(
                id = profile.id,
                name = profile.name,
                email = profile.email,
                role = profile.role,
                bio = profile.bio,
                version = profile.version
            )
        }
    }
}

data class MementoResponse(
    val userId: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val bio: String,
    val version: Int,
    val timestamp: LocalDateTime
) {
    companion object {
        fun fromDomain(memento: UserProfileMemento): MementoResponse {
            return MementoResponse(
                userId = memento.userId,
                name = memento.name,
                email = memento.email,
                role = memento.role,
                bio = memento.bio,
                version = memento.version,
                timestamp = memento.timestamp
            )
        }
    }
}

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
