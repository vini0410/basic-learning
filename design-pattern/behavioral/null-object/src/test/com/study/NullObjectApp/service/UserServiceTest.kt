package com.study.NullObjectApp.service

import com.study.NullObjectApp.domain.user.NullUser
import com.study.NullObjectApp.domain.user.RegisteredUser
import com.study.NullObjectApp.dto.CheckAccessRequest
import com.study.NullObjectApp.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserServiceTest {

    private lateinit var repository: UserRepository
    private lateinit var service: UserService

    @BeforeEach
    fun setUp() {
        repository = mockk()
        service = UserService(repository)
    }

    @Test
    fun `deve obter perfil de usuario cadastrado sem erros (happy path)`() {
        val user = RegisteredUser("U-100", "Alice", "a@test.com", "ADMIN", 0.1, setOf("READ"))
        every { repository.findById("U-100") } returns user

        val dto = service.getUserProfile("U-100")

        assertEquals("U-100", dto.id)
        assertFalse(dto.isAnonymous)
        assertEquals(0.1, dto.discountRate)
    }

    @Test
    fun `deve obter perfil de visitante anônimo quando usuario nao existir (edge case)`() {
        every { repository.findById("UNKNOWN") } returns NullUser()

        val dto = service.getUserProfile("UNKNOWN")

        assertEquals("GUEST-000", dto.id)
        assertTrue(dto.isAnonymous)
        assertEquals(0.0, dto.discountRate)
    }

    @Test
    fun `deve verificar acesso com seguranca para NullUser (edge case)`() {
        every { repository.findById(null) } returns NullUser()

        val response = service.checkUserAccess(CheckAccessRequest(null, "ADMIN"))

        assertEquals("GUEST-000", response.userId)
        assertFalse(response.hasAccess)
    }
}
