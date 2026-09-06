package com.study.NullObjectApp.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserRepositoryTest {

    private lateinit var repository: UserRepository

    @BeforeEach
    fun setUp() {
        repository = UserRepository()
    }

    @Test
    fun `deve retornar RegisteredUser quando o id for valido e existir no banco (happy path)`() {
        val user = repository.findById("U-100")

        assertFalse(user.isNull())
        assertEquals("Alice Admin", user.getName())
        assertEquals(0.15, user.getDiscountRate())
    }

    @Test
    fun `deve retornar NullUser quando o id nao for encontrado, for nulo ou em branco (edge case)`() {
        val userUnknown = repository.findById("INVALID-ID")
        assertTrue(userUnknown.isNull())
        assertEquals("GUEST-000", userUnknown.getId())

        val userNull = repository.findById(null)
        assertTrue(userNull.isNull())

        val userBlank = repository.findById("   ")
        assertTrue(userBlank.isNull())
    }
}
