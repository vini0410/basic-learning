package com.study.MementoApp.domain.model

import com.study.MementoApp.exception.InvalidMementoException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class UserProfileMementoTest {

    @Test
    fun `deve criar Memento imutavel com estado correto do Originator (happy path)`() {
        val profile = UserProfile(
            id = "USER-100",
            name = "Alice",
            email = "alice@test.com",
            role = UserRole.USER,
            bio = "Dev Kotlin",
            version = 1
        )

        val memento = profile.createMemento()

        assertEquals("USER-100", memento.userId)
        assertEquals("Alice", memento.name)
        assertEquals("alice@test.com", memento.email)
        assertEquals(UserRole.USER, memento.role)
        assertEquals("Dev Kotlin", memento.bio)
        assertEquals(1, memento.version)
        assertNotNull(memento.timestamp)
    }

    @Test
    fun `deve restaurar estado do Originator a partir do Memento (happy path)`() {
        val profile = UserProfile(
            id = "USER-100",
            name = "Alice Original",
            email = "alice@test.com",
            role = UserRole.USER,
            bio = "Bio 1",
            version = 1
        )

        val snapshotV1 = profile.createMemento()

        // Altera o perfil para V2
        profile.update("Alice Modificada", "alice.new@test.com", UserRole.ADMIN, "Bio 2")
        assertEquals(2, profile.version)
        assertEquals("Alice Modificada", profile.name)

        // Restaura para V1
        profile.restore(snapshotV1)

        assertEquals(1, profile.version)
        assertEquals("Alice Original", profile.name)
        assertEquals("alice@test.com", profile.email)
        assertEquals(UserRole.USER, profile.role)
        assertEquals("Bio 1", profile.bio)
    }

    @Test
    fun `deve lancar InvalidMementoException ao tentar aplicar Memento de outro usuario (edge case)`() {
        val profileAlice = UserProfile("USER-100", "Alice", "alice@test.com")
        val mementoBob = UserProfileMemento("USER-200", "Bob", "bob@test.com", UserRole.USER, "Bio", 1)

        val exception = assertThrows(InvalidMementoException::class.java) {
            profileAlice.restore(mementoBob)
        }

        assertEquals("O Memento fornecido pertence ao usuario 'USER-200', mas tentou ser aplicado ao usuario 'USER-100'", exception.message)
    }
}
