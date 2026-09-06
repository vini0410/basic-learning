package com.study.MementoApp.service

import com.study.MementoApp.domain.caretaker.UserProfileCaretaker
import com.study.MementoApp.domain.model.UserProfileMemento
import com.study.MementoApp.domain.model.UserRole
import com.study.MementoApp.dto.CreateProfileRequest
import com.study.MementoApp.dto.UpdateProfileRequest
import com.study.MementoApp.exception.NoHistoryToUndoException
import com.study.MementoApp.exception.UserProfileNotFoundException
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserProfileServiceTest {

    private lateinit var caretaker: UserProfileCaretaker
    private lateinit var service: UserProfileService

    @BeforeEach
    fun setUp() {
        caretaker = mockk()
        service = UserProfileService(caretaker)
    }

    @Test
    fun `deve criar perfil e salvar memento inicial no caretaker (happy path)`() {
        every { caretaker.saveMemento(any()) } just runs

        val request = CreateProfileRequest(
            name = "Carlos",
            email = "carlos@test.com",
            role = UserRole.USER,
            bio = "Engenheiro de Software"
        )

        val response = service.createProfile(request)

        assertNotNull(response.id)
        assertEquals("Carlos", response.name)
        assertEquals(1, response.version)
        verify(exactly = 1) { caretaker.saveMemento(any()) }
    }

    @Test
    fun `deve atualizar perfil incrementando a versao e salvando novo memento (happy path)`() {
        every { caretaker.saveMemento(any()) } just runs
        val created = service.createProfile(CreateProfileRequest("Carlos", "carlos@test.com"))

        val updateRequest = UpdateProfileRequest(
            name = "Carlos Silva",
            email = "carlos.silva@test.com",
            role = UserRole.ADMIN,
            bio = "Lead Eng"
        )

        val updatedResponse = service.updateProfile(created.id, updateRequest)

        assertEquals(2, updatedResponse.version)
        assertEquals("Carlos Silva", updatedResponse.name)
        assertEquals(UserRole.ADMIN, updatedResponse.role)
        verify(exactly = 2) { caretaker.saveMemento(any()) }
    }

    @Test
    fun `deve desfazer alteracao restaurando estado do memento anterior (happy path)`() {
        every { caretaker.saveMemento(any()) } just runs
        val created = service.createProfile(CreateProfileRequest("Original Name", "orig@test.com"))

        val updateRequest = UpdateProfileRequest("New Name", "new@test.com", UserRole.USER, "Bio")
        val updated = service.updateProfile(created.id, updateRequest)
        assertEquals(2, updated.version)

        val previousMemento = UserProfileMemento(created.id, "Original Name", "orig@test.com", UserRole.USER, "", 1)
        every { caretaker.getPreviousMemento(created.id, currentVersion = 2) } returns previousMemento

        val undoneResponse = service.undoUpdate(created.id)

        assertEquals(1, undoneResponse.version)
        assertEquals("Original Name", undoneResponse.name)
        assertEquals("orig@test.com", undoneResponse.email)
    }

    @Test
    fun `deve lancar NoHistoryToUndoException quando nao houver memento anterior (edge case)`() {
        every { caretaker.saveMemento(any()) } just runs
        val created = service.createProfile(CreateProfileRequest("Initial", "init@test.com"))

        every { caretaker.getPreviousMemento(created.id, currentVersion = 1) } returns null

        assertThrows(NoHistoryToUndoException::class.java) {
            service.undoUpdate(created.id)
        }
    }

    @Test
    fun `deve lancar UserProfileNotFoundException ao buscar usuario inexistente (edge case)`() {
        assertThrows(UserProfileNotFoundException::class.java) {
            service.getProfile("NON-EXISTENT")
        }
    }
}
