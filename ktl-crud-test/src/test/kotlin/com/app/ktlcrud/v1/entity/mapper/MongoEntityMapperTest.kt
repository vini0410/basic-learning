package com.app.ktlcrud.v1.entity.mapper

import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import com.app.ktlcrud.v1.entity.MongoAddressEntity
import com.app.ktlcrud.v1.entity.MongoUserEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MongoEntityMapperTest {

    @Test
    fun `User toMongoEntity mapping should correctly map fields and collect address IDs`() {
        // Arrange
        val addressModel1 = Address(id = "addr1", userId = "user1", street = "123 Main St", number = "10", complement = "Apt 1", code = "A1")
        val addressModel2 = Address(id = "addr2", userId = "user1", street = "456 Oak Ave", number = "20", complement = null, code = "A2")
        val userModel = User(
            id = "user1",
            name = "John Doe",
            surname = "Doe",
            nickname = "JD",
            password = "securepassword",
            addresses = listOf(addressModel1, addressModel2) // Addresses' IDs should be collected
        )

        // Act
        val mongoUserEntity = userModel.toMongoEntity()

        // Assert
        assertEquals("user1", mongoUserEntity.id)
        assertEquals("John Doe", mongoUserEntity.name)
        assertEquals("Doe", mongoUserEntity.surname)
        assertEquals("JD", mongoUserEntity.nickname)
        assertEquals("securepassword", mongoUserEntity.password)
        assertEquals(listOf("addr1", "addr2"), mongoUserEntity.addressIds)
    }

    @Test
    fun `User toMongoEntity mapping should handle null ID and empty addresses`() {
        // Arrange
        val userModel = User(id = null, name = "New User", password = "password", surname = "N", nickname = "NU", addresses = emptyList())

        // Act
        val mongoUserEntity = userModel.toMongoEntity()

        // Assert
        assertNull(mongoUserEntity.id)
        assertEquals("New User", mongoUserEntity.name)
        assertTrue(mongoUserEntity.addressIds.isEmpty())
    }

    @Test
    fun `Address toMongoEntity mapping should correctly map fields with userId`() {
        // Arrange
        val addressModel = Address(id = "addr101", userId = "user1", code = "C3", street = "789 Pine Ln", number = "30", complement = "Unit 5")
        val userId = "user1"

        // Act
        val mongoAddressEntity = addressModel.toMongoEntity(userId)

        // Assert
        assertEquals("addr101", mongoAddressEntity.id)
        assertEquals("user1", mongoAddressEntity.userId)
        assertEquals("C3", mongoAddressEntity.code)
        assertEquals("789 Pine Ln", mongoAddressEntity.street)
        assertEquals("30", mongoAddressEntity.number)
        assertEquals("Unit 5", mongoAddressEntity.complement)
    }

    @Test
    fun `Address toMongoEntity mapping should handle null complement`() {
        // Arrange
        val addressModel = Address(id = null, userId = "user2", code = "D4", street = "101 Maple Dr", number = "40", complement = null)
        val userId = "user2"

        // Act
        val mongoAddressEntity = addressModel.toMongoEntity(userId)

        // Assert
        assertNull(mongoAddressEntity.id)
        assertEquals("user2", mongoAddressEntity.userId)
        assertEquals("D4", mongoAddressEntity.code)
        assertEquals("101 Maple Dr", mongoAddressEntity.street)
        assertEquals("40", mongoAddressEntity.number)
        assertNull(mongoAddressEntity.complement)
    }

    @Test
    fun `MongoUserEntity toModel mapping should correctly map fields`() {
        // Arrange
        val mongoUserEntity = MongoUserEntity(
            id = "user1",
            name = "John Doe",
            surname = "Doe",
            nickname = "JD",
            password = "securepassword",
            addressIds = listOf("addr1", "addr2")
        )

        // Act
        val userModel = mongoUserEntity.toModel()

        // Assert
        assertEquals("user1", userModel.id)
        assertEquals("John Doe", userModel.name)
        assertEquals("Doe", userModel.surname)
        assertEquals("JD", userModel.nickname)
        assertEquals("securepassword", userModel.password)
        assertTrue(userModel.addresses.isEmpty()) // Addresses are intentionally not mapped here
    }

    @Test
    fun `MongoUserEntity toModel mapping should handle null fields`() {
        // Arrange
        val mongoUserEntity = MongoUserEntity(
            id = null,
            name = "",
            surname = "",
            nickname = "",
            password = "",
            addressIds = emptyList()
        )

        // Act
        val userModel = mongoUserEntity.toModel()

        // Assert
        assertNull(userModel.id)
        assertEquals("", userModel.name)
        assertEquals("", userModel.surname)
        assertEquals("", userModel.nickname)
        assertEquals("", userModel.password)
        assertTrue(userModel.addresses.isEmpty())
    }

    @Test
    fun `MongoAddressEntity toModel mapping should correctly map fields`() {
        // Arrange
        val mongoAddressEntity = MongoAddressEntity(id = "addr101", userId = "user1", code = "C3", street = "789 Pine Ln", number = "30", complement = "Unit 5")

        // Act
        val addressModel = mongoAddressEntity.toModel()

        // Assert
        assertEquals("addr101", addressModel.id)
        assertEquals("user1", addressModel.userId)
        assertEquals("C3", addressModel.code)
        assertEquals("789 Pine Ln", addressModel.street)
        assertEquals("30", addressModel.number)
        assertEquals("Unit 5", addressModel.complement)
    }

    @Test
    fun `MongoAddressEntity toModel mapping should handle null complement and userId`() {
        // Arrange
        val mongoAddressEntity = MongoAddressEntity(id = "addr102", userId = "user2", code = "D4", street = "101 Maple Dr", number = "40", complement = null)

        // Act
        val addressModel = mongoAddressEntity.toModel()

        // Assert
        assertEquals("addr102", addressModel.id)
        assertEquals("user2", addressModel.userId)
        assertEquals("D4", addressModel.code)
        assertEquals("101 Maple Dr", addressModel.street)
        assertEquals("40", addressModel.number)
        assertNull(addressModel.complement)
    }

    @Test
    fun `MongoAddressEntity toModel mapping should handle null fields`() {
        // Arrange
        val mongoAddressEntity = MongoAddressEntity(id = null, userId = "", code = "", street = "", number = "", complement = null)

        // Act
        val addressModel = mongoAddressEntity.toModel()

        // Assert
        assertNull(addressModel.id)
        assertEquals("", addressModel.userId)
        assertEquals("", addressModel.code)
        assertEquals("", addressModel.street)
        assertEquals("", addressModel.number)
        assertNull(addressModel.complement)
    }
}