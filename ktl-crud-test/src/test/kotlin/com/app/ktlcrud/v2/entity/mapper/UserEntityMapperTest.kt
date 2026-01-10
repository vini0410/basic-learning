package com.app.ktlcrud.v2.entity.mapper

import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import com.app.ktlcrud.v2.entity.AddressEntity
import com.app.ktlcrud.v2.entity.UserEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserEntityMapperTest {

    @Test
    fun `User toEntity mapping should correctly map fields`() {
        // Arrange
        val addressModel1 = Address(id = "addr1", userId = "user1", code = "A1", street = "123 Main St", number = "10", complement = "Apt 1")
        val addressModel2 = Address(id = "addr2", userId = "user1", code = "B2", street = "456 Oak Ave", number = "20", complement = null)
        val userModel = User(
            id = "user1",
            name = "John Doe",
            surname = "Doe",
            nickname = "JD",
            password = "securepassword", // Password should be mapped to entity
            addresses = listOf(addressModel1, addressModel2) // Addresses are not mapped by User.toEntity()
        )

        // Act
        val userEntity = userModel.toEntity()

        // Assert
        assertEquals("user1", userEntity.id)
        assertEquals("John Doe", userEntity.name)
        assertEquals("Doe", userEntity.surname)
        assertEquals("JD", userEntity.nickname)
        assertEquals("securepassword", userEntity.password)
        // Email is NOT in UserEntity, so it should not be mapped.
        // Addresses are not mapped by User.toEntity()
    }

    @Test
    fun `User toEntity mapping should handle null ID and nullable fields`() {
        // Arrange
        val userModel = User(id = null, name = "", surname = "", nickname = "", password = "", addresses = emptyList())

        // Act
        val userEntity = userModel.toEntity()

        // Assert
        assertNull(userEntity.id)
        assertEquals("", userEntity.name)
        assertEquals("", userEntity.surname)
        assertEquals("", userEntity.nickname)
        assertEquals("", userEntity.password)
        // Email is not mapped to UserEntity
    }

    @Test
    fun `Address toEntity mapping should correctly map fields with userId`() {
        // Arrange
        val addressModel = Address(id = "addr101", userId = "user1", code = "C3", street = "789 Pine Ln", number = "30", complement = "Unit 5")
        val userId = "user1"

        // Act
        val addressEntity = addressModel.toEntity(userId)

        // Assert
        assertEquals("addr101", addressEntity.id)
        assertEquals("user1", addressEntity.userId)
        assertEquals("C3", addressEntity.code)
        assertEquals("789 Pine Ln", addressEntity.street)
        assertEquals("30", addressEntity.number)
        assertEquals("Unit 5", addressEntity.complement)
    }

    @Test
    fun `Address toEntity mapping should handle null complement and explicit userId`() {
        // Arrange
        val addressModel = Address(id = null, userId = "null", code = "D4", street = "101 Maple Dr", number = "40", complement = null)
        val userId = "user2" // Explicit userId passed from service

        // Act
        val addressEntity = addressModel.toEntity(userId)

        // Assert
        assertNull(addressEntity.id)
        assertEquals("user2", addressEntity.userId) // Should use the passed userId
        assertEquals("D4", addressEntity.code)
        assertEquals("101 Maple Dr", addressEntity.street)
        assertEquals("40", addressEntity.number)
        assertNull(addressEntity.complement)
    }
    
    @Test
    fun `Address toEntity mapping should handle null userId passed from service`() {
        // Arrange
        val addressModel = Address(id = null, userId = "null", code = "E5", street = "202 Elm St", number = "50", complement = null)
        
        // Act
        val addressEntity = addressModel.toEntity(null) // Explicitly pass null userId

        // Assert
        assertNull(addressEntity.id)
        assertEquals("", addressEntity.userId) // Should default to empty string if null userId is passed
        assertEquals("E5", addressEntity.code)
    }


    @Test
    fun `UserEntity toModel mapping should correctly map fields`() {
        // Arrange
        val userEntity = UserEntity(
            id = "user1",
            name = "John Doe",
            surname = "Doe",
            nickname = "JD",
            password = "securepassword"
            // Email is NOT in UserEntity
        )

        // Act
        val userModel = userEntity.toModel()

        // Assert
        assertEquals("user1", userModel.id)
        assertEquals("John Doe", userModel.name)
        assertEquals("Doe", userModel.surname)
        assertEquals("JD", userModel.nickname)
        assertEquals("securepassword", userModel.password)
        assertTrue(userModel.addresses.isEmpty()) // Addresses are intentionally not mapped here
    }
    
    @Test
    fun `UserEntity toModel mapping should handle null fields`() {
        // Arrange
        val userEntity = UserEntity(
            id = null,
            name = "",
            surname = "",
            nickname = "",
            password = ""
        )

        // Act
        val userModel = userEntity.toModel()

        // Assert
        assertNull(userModel.id)
        assertEquals("", userModel.name)
        assertEquals("", userModel.surname)
        assertEquals("", userModel.nickname)
        assertEquals("", userModel.password)
        assertTrue(userModel.addresses.isEmpty())
    }


    @Test
    fun `AddressEntity toModel mapping should correctly map fields`() {
        // Arrange
        val addressEntity = AddressEntity(id = "101", userId = "user1", code = "C3", street = "789 Pine Ln", number = "30", complement = "Unit 5")

        // Act
        val addressModel = addressEntity.toModel()

        // Assert
        assertEquals("101", addressModel.id)
        assertEquals("user1", addressModel.userId)
        assertEquals("C3", addressModel.code)
        assertEquals("789 Pine Ln", addressModel.street)
        assertEquals("30", addressModel.number)
        assertEquals("Unit 5", addressModel.complement)
    }

    @Test
    fun `AddressEntity toModel mapping should handle null complement and userId`() {
        // Arrange
        val addressEntity = AddressEntity(id = "102", userId = "user2", code = "D4", street = "101 Maple Dr", number = "40", complement = null)

        // Act
        val addressModel = addressEntity.toModel()

        // Assert
        assertEquals("102", addressModel.id)
        assertEquals("user2", addressModel.userId)
        assertEquals("D4", addressModel.code)
        assertEquals("101 Maple Dr", addressModel.street)
        assertEquals("40", addressModel.number)
        assertNull(addressModel.complement)
    }
    
    @Test
    fun `AddressEntity toModel mapping should handle null fields`() {
        // Arrange
        val addressEntity = AddressEntity(id = null, userId = "", code = "", street = "", number = "", complement = null)

        // Act
        val addressModel = addressEntity.toModel()

        // Assert
        assertNull(addressModel.id)
        assertEquals("", addressModel.userId)
        assertEquals("", addressModel.code)
        assertEquals("", addressModel.street)
        assertEquals("", addressModel.number)
        assertNull(addressModel.complement)
    }
}
