package com.study.NullObjectApp.domain.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class NullUserTest {

    @Test
    fun `RegisteredUser deve retornar informacoes e permissoes reais (happy path)`() {
        val user = RegisteredUser("U-1", "Alice", "alice@test.com", "ADMIN", 0.10, setOf("READ", "WRITE"))

        assertEquals("U-1", user.getId())
        assertEquals("Alice", user.getName())
        assertEquals("ADMIN", user.getRole())
        assertEquals(0.10, user.getDiscountRate())
        assertTrue(user.hasAccess("READ"))
        assertTrue(user.hasAccess("WRITE"))
        assertFalse(user.hasAccess("DELETE"))
        assertFalse(user.isNull())
    }

    @Test
    fun `NullUser deve retornar comportamento padrao seguro sem lancar excecoes (happy path)`() {
        val nullUser = NullUser()

        assertEquals("GUEST-000", nullUser.getId())
        assertEquals("Visitante Anônimo", nullUser.getName())
        assertEquals("GUEST", nullUser.getRole())
        assertEquals(0.0, nullUser.getDiscountRate())
        assertFalse(nullUser.hasAccess("READ"))
        assertFalse(nullUser.hasAccess("ADMIN"))
        assertTrue(nullUser.isNull())
    }
}
