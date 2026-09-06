package com.study.MementoApp.domain.model

import com.study.MementoApp.exception.InvalidMementoException

/**
 * Originator: Entidade cujo estado interno sera salvo em snapshots (Mementos) e pode ser restaurado.
 */
class UserProfile(
    val id: String,
    var name: String,
    var email: String,
    var role: UserRole = UserRole.USER,
    var bio: String = "",
    var version: Int = 1
) {
    /**
     * Cria um novo Memento (snapshot) contendo uma copia imutavel do estado atual.
     */
    fun createMemento(): UserProfileMemento {
        return UserProfileMemento(
            userId = id,
            name = name,
            email = email,
            role = role,
            bio = bio,
            version = version
        )
    }

    /**
     * Restaura o estado a partir de um Memento fornecido.
     */
    fun restore(memento: UserProfileMemento) {
        if (memento.userId != this.id) {
            throw InvalidMementoException("O Memento fornecido pertence ao usuario '${memento.userId}', mas tentou ser aplicado ao usuario '${this.id}'")
        }
        this.name = memento.name
        this.email = memento.email
        this.role = memento.role
        this.bio = memento.bio
        this.version = memento.version
    }

    /**
     * Atualiza os dados do perfil e incrementa a versao.
     */
    fun update(newName: String, newEmail: String, newRole: UserRole, newBio: String) {
        require(newName.isNotBlank()) { "Nome nao pode ser em branco" }
        require(newEmail.contains("@")) { "Email invalido" }

        this.name = newName
        this.email = newEmail
        this.role = newRole
        this.bio = newBio
        this.version += 1
    }
}
