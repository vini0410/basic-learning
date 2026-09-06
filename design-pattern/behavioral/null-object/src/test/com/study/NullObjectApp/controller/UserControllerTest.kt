package com.study.NullObjectApp.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.NullObjectApp.dto.AccessCheckResponse
import com.study.NullObjectApp.dto.CheckAccessRequest
import com.study.NullObjectApp.dto.UserProfileDto
import com.study.NullObjectApp.service.UserService
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userService: UserService

    @TestConfiguration
    class TestConfig {
        @Bean
        fun userService(): UserService = mockk()
    }

    @Test
    fun `GET api users profile sem parametro deve retornar perfil de NullUser com 200 OK (happy path)`() {
        val guestDto = UserProfileDto("GUEST-000", "Visitante Anônimo", "guest@system.local", "GUEST", 0.0, true)
        every { userService.getUserProfile(null) } returns guestDto

        mockMvc.perform(get("/api/users/profile"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("GUEST-000"))
            .andExpect(jsonPath("$.isAnonymous").value(true))
    }

    @Test
    fun `POST api users check-access deve retornar 200 OK (happy path)`() {
        val request = CheckAccessRequest("U-100", "READ")
        val response = AccessCheckResponse("U-100", "READ", true)

        every { userService.checkUserAccess(any()) } returns response

        mockMvc.perform(
            post("/api/users/check-access")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.hasAccess").value(true))
    }
}
