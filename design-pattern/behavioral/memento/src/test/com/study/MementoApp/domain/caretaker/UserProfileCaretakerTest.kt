package com.study.MementoApp.domain.caretaker

import com.study.MementoApp.domain.model.UserProfileMemento
import com.study.MementoApp.domain.model.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserProfileCaretakerTest {

    private lateinit var caretaker: UserProfileCaretaker

    @BeforeEach
    fun setUp() {
        caretaker = UserProfileCaretaker()
    }

    @Test
    fun `deve armazenar e recuperar historico de mementos em ordem (happy path)`() {
        val m1 = UserProfileMemento("U-1", "V1", "v1@test.com", UserRole.USER, "", 1)
        val m2 = UserProfileMemento("U-1", "V2", "v2@test.com", UserRole.ADMIN, "", 2)

        caretaker.saveMemento(m1)
        caretaker.saveMemento(m2)

        val history = caretaker.getHistory("U-1")
        assertEquals(2, history.size)
        assertEquals(1, history[0].version)
        assertEquals(2, history[1].version)
    }

    @Test
    fun `deve buscar memento por versao especifica (happy path)`() {
        val m1 = UserProfileMemento("U-1", "V1", "v1@test.com", UserRole.USER, "", 1)
        val m2 = UserProfileMemento("U-1", "V2", "v2@test.com", UserRole.ADMIN, "", 2)

        caretaker.saveMemento(m1)
        caretaker.saveMemento(m2)

        val found = caretaker.getMementoByVersion("U-1", 1)
        assertNotNull(found)
        assertEquals("V1", found?.name)
    }

    @Test
    fun `deve retornar o memento imediatamente anterior para a operacao undo (happy path)`() {
        val m1 = UserProfileMemento("U-1", "V1", "v1@test.com", UserRole.USER, "", 1)
        val m2 = UserProfileMemento("U-1", "V2", "v2@test.com", UserRole.ADMIN, "", 2)
        val m3 = UserProfileMemento("U-1", "V3", "v3@test.com", UserRole.ADMIN, "", 3)

        caretaker.saveMemento(m1)
        caretaker.saveMemento(m2)
        caretaker.saveMemento(m3)

        val previous = caretaker.getPreviousMemento("U-1", currentVersion = 3)
        assertNotNull(previous)
        assertEquals(2, previous?.version)
        assertEquals("V2", previous?.name)
    }

    @Test
    fun `deve retornar null se nao houver memento anterior (edge case)`() {
        val m1 = UserProfileMemento("U-1", "V1", "v1@test.com", UserRole.USER, "", 1)
        caretaker.saveMemento(m1)

        val previous = caretaker.getPreviousMemento("U-1", currentVersion = 1)
        assertNull(previous)
    }
}
