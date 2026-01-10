package com.app.ktlcrud.controller.mapper

import com.app.ktlcrud.controller.dto.AddressDTO
import com.app.ktlcrud.controller.dto.UserRequestDTO
import com.app.ktlcrud.controller.dto.UserResponseDTO
import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserDtoMapperTest {

    @Test
    fun `UserRequestDTO toModel mapping should correctly map all fields`() {
        // Arrange
        val addressRequestDTO1 = AddressDTO(code = "A1", street = "123 Main St", number = "10", complement = "Apt 1")
        val addressRequestDTO2 = AddressDTO(code = "B2", street = "456 Oak Ave", number = "20", complement = null)
        val userRequestDTO = UserRequestDTO(
            name = "John Doe",
            surname = "Doe",
            nickname = "JD",
            password = "securepassword", // Password field is present in UserRequestDTO
            addresses = listOf(addressRequestDTO1, addressRequestDTO2)
        )

        // Act
        val userModel = userRequestDTO.toModel()

        // Assert
        assertEquals("John Doe", userModel.name)
        assertEquals("Doe", userModel.surname)
        assertEquals("JD", userModel.nickname)
        assertEquals("securepassword", userModel.password) // Password should be mapped to model
        assertEquals(2, userModel.addresses.size)
        assertEquals("A1", userModel.addresses[0].code)
        assertEquals("123 Main St", userModel.addresses[0].street)
        assertEquals("10", userModel.addresses[0].number)
        assertEquals("Apt 1", userModel.addresses[0].complement)
        assertEquals("B2", userModel.addresses[1].code)
        assertEquals("456 Oak Ave", userModel.addresses[1].street)
        assertEquals("20", userModel.addresses[1].number)
        assertNull(userModel.addresses[1].complement)
        assertNull(userModel.id) // ID should be null when mapping from request
    }
    
    @Test
    fun `UserRequestDTO toModel mapping should handle empty addresses`() {
        // Arrange
        val userRequestDTO = UserRequestDTO(
            name = "Jane Smith",
            surname = "", nickname = "", password = "",
            addresses = emptyList()
        )

        // Act
        val userModel = userRequestDTO.toModel()

        // Assert
        assertEquals("Jane Smith", userModel.name)
        assertEquals("", userModel.surname)
        assertEquals("", userModel.nickname)
        assertEquals("", userModel.password)
        assertTrue(userModel.addresses.isEmpty())
        assertNull(userModel.id)
    }

    @Test
    fun `AddressDTO toModel mapping should correctly map fields`() {
        // Arrange
        val addressDTO = AddressDTO(code = "C3", street = "789 Pine Ln", number = "30", complement = "Unit 5")

        // Act
        val addressModel = addressDTO.toModel()

        // Assert
        assertEquals("C3", addressModel.code)
        assertEquals("789 Pine Ln", addressModel.street)
        assertEquals("30", addressModel.number)
        assertEquals("Unit 5", addressModel.complement)
        assertNull(addressModel.id) // ID should be null when mapping from request
        assertEquals("", addressModel.userId) // userId should be empty initially as it's not in AddressDTO
    }
    
    @Test
    fun `AddressDTO toModel mapping should handle null complement`() {
        // Arrange
        val addressDTO = AddressDTO(code = "D4", street = "101 Maple Dr", number = "40", complement = null)

        // Act
        val addressModel = addressDTO.toModel()

        // Assert
        assertEquals("D4", addressModel.code)
        assertEquals("101 Maple Dr", addressModel.street)
        assertEquals("40", addressModel.number)
        assertNull(addressModel.complement)
    }

    @Test
    fun `User toResponseDTO mapping should correctly map fields`() {
        // Arrange
        val addressModel1 = Address(id = "addr1", userId = "user1", code = "A1", street = "123 Main St", number = "10", complement = "Apt 1")
        val addressModel2 = Address(id = "addr2", userId = "user1", code = "B2", street = "456 Oak Ave", number = "20", complement = null)
        val userModel = User(
            id = "user1",
            name = "John Doe",
            surname = "Doe",
            nickname = "JD",
            password = "securepassword", // Password should NOT be in ResponseDTO
            addresses = listOf(addressModel1, addressModel2)
        )

        // Act
        val userResponseDTO = userModel.toResponseDTO()

        // Assert
        assertEquals("user1", userResponseDTO.id)
        assertEquals("John Doe", userResponseDTO.name)
        assertEquals("Doe", userResponseDTO.surname)
        assertEquals("JD", userResponseDTO.nickname)
        // Password should NOT be in the response DTO
        // Email is also NOT in UserResponseDTO based on its definition.
        assertEquals(2, userResponseDTO.addresses.size)
        assertEquals("A1", userResponseDTO.addresses[0].code)
        assertEquals("123 Main St", userResponseDTO.addresses[0].street)
        assertEquals("10", userResponseDTO.addresses[0].number)
        assertEquals("Apt 1", userResponseDTO.addresses[0].complement)
        assertEquals("B2", userResponseDTO.addresses[1].code)
        assertEquals("456 Oak Ave", userResponseDTO.addresses[1].street)
        assertEquals("20", userResponseDTO.addresses[1].number)
        assertNull(userResponseDTO.addresses[1].complement)
    }
    
    @Test
    fun `User toResponseDTO mapping should handle empty addresses`() {
        // Arrange
        val userModel = User(id = "user2", name = "Jane Smith", surname = "S", nickname = "JS", password = "p", addresses = emptyList())

        // Act
        val userResponseDTO = userModel.toResponseDTO()

        // Assert
        assertEquals("user2", userResponseDTO.id)
        assertEquals("Jane Smith", userResponseDTO.name)
        assertTrue(userResponseDTO.addresses.isEmpty())
    }

    @Test
    fun `Address toDTO mapping should correctly map fields`() {
        // Arrange
        val addressModel = Address(id = "addr1", userId = "user1", code = "C3", street = "789 Pine Ln", number = "30", complement = "Unit 5")

        // Act
        val addressDTO = addressModel.toDTO()

        // Assert
        assertEquals("C3", addressDTO.code)
        assertEquals("789 Pine Ln", addressDTO.street)
        assertEquals("30", addressDTO.number)
        assertEquals("Unit 5", addressDTO.complement)
    }
    
    @Test
    fun `Address toDTO mapping should handle null complement`() {
        // Arrange
        val addressModel = Address(id = "addr2", userId = "user1", code = "D4", street = "101 Maple Dr", number = "40", complement = null)

        // Act
        val addressDTO = addressModel.toDTO()

        // Assert
        assertEquals("D4", addressDTO.code)
        assertEquals("101 Maple Dr", addressDTO.street)
        assertEquals("40", addressDTO.number)
        assertNull(addressDTO.complement)
    }
}
