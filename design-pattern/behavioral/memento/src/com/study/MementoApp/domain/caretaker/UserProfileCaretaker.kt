package com.study.MementoApp.domain.caretaker

import com.study.MementoApp.domain.model.UserProfileMemento
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger {}

/**
 * Caretaker: Responsavel por guardar e gerenciar a lista de Mementos (snapshots) sem alterar seu conteudo.
 */
@Component
class UserProfileCaretaker {

    // Chave: userId -> Lista de Mementos salvos em ordem cronologica
    private val history = ConcurrentHashMap<String, MutableList<UserProfileMemento>>()

    fun saveMemento(memento: UserProfileMemento) {
        history.computeIfAbsent(memento.userId) { mutableListOf() }.add(memento)
        logger.info { "Memento da versao ${memento.version} armazenado no Caretaker para usuario '${memento.userId}'" }
    }

    fun getHistory(userId: String): List<UserProfileMemento> {
        return history[userId]?.toList() ?: emptyList()
    }

    fun getMementoByVersion(userId: String, version: Int): UserProfileMemento? {
        return history[userId]?.firstOrNull { it.version == version }
    }

    fun getPreviousMemento(userId: String, currentVersion: Int): UserProfileMemento? {
        val userHistory = history[userId] ?: return null
        return userHistory.filter { it.version < currentVersion }.maxByOrNull { it.version }
    }
}
