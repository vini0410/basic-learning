package com.study.MementoApp.service

import com.study.MementoApp.domain.caretaker.UserProfileCaretaker
import com.study.MementoApp.domain.model.UserProfile
import com.study.MementoApp.dto.CreateProfileRequest
import com.study.MementoApp.dto.MementoResponse
import com.study.MementoApp.dto.UpdateProfileRequest
import com.study.MementoApp.dto.UserProfileResponse
import com.study.MementoApp.exception.MementoNotFoundException
import com.study.MementoApp.exception.NoHistoryToUndoException
import com.study.MementoApp.exception.UserProfileNotFoundException
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger {}

@Service
class UserProfileService(
    private val caretaker: UserProfileCaretaker
) {

    private val profiles = ConcurrentHashMap<String, UserProfile>()

    fun createProfile(request: CreateProfileRequest): UserProfileResponse {
        require(request.name.isNotBlank()) { "Nome nao pode ser em branco" }
        require(request.email.contains("@")) { "Email invalido" }

        val profile = UserProfile(
            id = UUID.randomUUID().toString(),
            name = request.name,
            email = request.email,
            role = request.role,
            bio = request.bio,
            version = 1
        )
        profiles[profile.id] = profile

        // Salvar snapshot inicial v1 no Caretaker
        caretaker.saveMemento(profile.createMemento())
        logger.info { "Perfil criado com ID '${profile.id}' e versao v1 salva no Caretaker" }

        return UserProfileResponse.fromDomain(profile)
    }

    fun updateProfile(userId: String, request: UpdateProfileRequest): UserProfileResponse {
        val profile = profiles[userId] ?: throw UserProfileNotFoundException(userId)

        // 1. Atualizar entidade (incrementa versao)
        profile.update(request.name, request.email, request.role, request.bio)

        // 2. Criar e salvar Memento no Caretaker
        val memento = profile.createMemento()
        caretaker.saveMemento(memento)

        logger.info { "Perfil '${userId}' atualizado para versao v${profile.version}" }
        return UserProfileResponse.fromDomain(profile)
    }

    fun undoUpdate(userId: String): UserProfileResponse {
        val profile = profiles[userId] ?: throw UserProfileNotFoundException(userId)

        val previousMemento = caretaker.getPreviousMemento(userId, profile.version)
            ?: throw NoHistoryToUndoException(userId)

        // Restaura Originator com o Memento anterior
        profile.restore(previousMemento)

        logger.info { "Perfil '${userId}' desfez alteracao (Undo) e retornou para versao v${profile.version}" }
        return UserProfileResponse.fromDomain(profile)
    }

    fun restoreVersion(userId: String, version: Int): UserProfileResponse {
        val profile = profiles[userId] ?: throw UserProfileNotFoundException(userId)

        val targetMemento = caretaker.getMementoByVersion(userId, version)
            ?: throw MementoNotFoundException(userId, version)

        // Restaura o perfil com a versao especifica
        profile.restore(targetMemento)

        logger.info { "Perfil '${userId}' restaurado para versao especifica v${version}" }
        return UserProfileResponse.fromDomain(profile)
    }

    fun getHistory(userId: String): List<MementoResponse> {
        if (!profiles.containsKey(userId)) {
            throw UserProfileNotFoundException(userId)
        }
        return caretaker.getHistory(userId).map { MementoResponse.fromDomain(it) }
    }

    fun getProfile(userId: String): UserProfileResponse {
        val profile = profiles[userId] ?: throw UserProfileNotFoundException(userId)
        return UserProfileResponse.fromDomain(profile)
    }
}
