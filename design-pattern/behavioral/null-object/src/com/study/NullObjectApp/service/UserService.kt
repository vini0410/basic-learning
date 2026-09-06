package com.study.NullObjectApp.service

import com.study.NullObjectApp.dto.AccessCheckResponse
import com.study.NullObjectApp.dto.CheckAccessRequest
import com.study.NullObjectApp.dto.UserProfileDto
import com.study.NullObjectApp.repository.UserRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserProfile(userId: String?): UserProfileDto {
        // Nenhuma checagem `if (user == null)` necessaria! O repositorio retorna um NullUser seguro.
        val user = userRepository.findById(userId)
        logger.info { "Perfil solicitado para userId='$userId'. Nome retornado: '${user.getName()}', isNull: ${user.isNull()}" }
        return UserProfileDto.fromDomain(user)
    }

    fun checkUserAccess(request: CheckAccessRequest): AccessCheckResponse {
        val user = userRepository.findById(request.userId)
        // Execucao limpa e sem NullPointerException mesmo para usuarios inexistentes ou anônimos
        val hasAccess = user.hasAccess(request.permission)

        logger.info { "Checagem de permissao '${request.permission}' para usuario '${user.getId()}': $hasAccess" }

        return AccessCheckResponse(
            userId = user.getId(),
            permission = request.permission,
            hasAccess = hasAccess
        )
    }
}
