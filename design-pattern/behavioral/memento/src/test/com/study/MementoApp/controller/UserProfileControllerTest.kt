package com.study.MementoApp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.MementoApp.domain.model.UserRole
import com.study.MementoApp.dto.CreateProfileRequest
import com.study.MementoApp.dto.MementoResponse
import com.study.MementoApp.dto.UpdateProfileRequest
import com.study.MementoApp.dto.UserProfileResponse
import com.study.MementoApp.exception.UserProfileNotFoundException
import com.study.MementoApp.service.UserProfileService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(UserProfileController::class)
class UserProfileControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var profileService: UserProfileService

    @TestConfiguration
    class TestConfig {
        @Bean
        fun userProfileService(): UserProfileService = mockk()
    }

    @Test
    fun `POST api profiles deve criar perfil e retornar 201 Created (happy path)`() {
        val request = CreateProfileRequest("Daniel", "daniel@test.com", UserRole.USER, "Bio")
        val response = UserProfileResponse("USER-100", "Daniel", "daniel@test.com", UserRole.USER, "Bio", 1)

        every { profileService.createProfile(any()) } returns response

        mockMvc.perform(
            post("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value("USER-100"))
            .andExpect(jsonPath("$.version").value(1))
    }

    @Test
    fun `PUT api profiles id deve atualizar perfil e retornar 200 OK (happy path)`() {
        val request = UpdateProfileRequest("Daniel V2", "daniel.v2@test.com", UserRole.ADMIN, "New Bio")
        val response = UserProfileResponse("USER-100", "Daniel V2", "daniel.v2@test.com", UserRole.ADMIN, "New Bio", 2)

        every { profileService.updateProfile("USER-100", any()) } returns response

        mockMvc.perform(
            put("/api/profiles/USER-100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.version").value(2))
            .andExpect(jsonPath("$.name").value("Daniel V2"))
    }

    @Test
    fun `POST api profiles id undo deve desfazer alteracao e retornar 200 OK (happy path)`() {
        val response = UserProfileResponse("USER-100", "Daniel", "daniel@test.com", UserRole.USER, "Bio", 1)

        every { profileService.undoUpdate("USER-100") } returns response

        mockMvc.perform(post("/api/profiles/USER-100/undo"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.version").value(1))
            .andExpect(jsonPath("$.name").value("Daniel"))
    }

    @Test
    fun `GET api profiles id history deve retornar lista de mementos (happy path)`() {
        val m1 = MementoResponse("USER-100", "Daniel V1", "d1@test.com", UserRole.USER, "", 1, LocalDateTime.now())
        val m2 = MementoResponse("USER-100", "Daniel V2", "d2@test.com", UserRole.ADMIN, "", 2, LocalDateTime.now())

        every { profileService.getHistory("USER-100") } returns listOf(m1, m2)

        mockMvc.perform(get("/api/profiles/USER-100/history"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].version").value(1))
            .andExpect(jsonPath("$[1].version").value(2))
    }

    @Test
    fun `GET api profiles id deve retornar 404 Not Found quando usuario nao existe (edge case)`() {
        every { profileService.getProfile("NON-EXISTENT") } throws UserProfileNotFoundException("NON-EXISTENT")

        mockMvc.perform(get("/api/profiles/NON-EXISTENT"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Not Found"))
    }
}
