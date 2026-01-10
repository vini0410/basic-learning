package com.app.ktlcrud.service

import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import com.app.ktlcrud.port.UserServicePort
import com.app.ktlcrud.v1.service.MongoUserService
import com.app.ktlcrud.v2.service.PostgresUserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class UserDatabaseDispatcherTest {

    private val mongoUserService: MongoUserService = mockk()
    private val postgresUserService: PostgresUserService = mockk()

    // Sample data - User model has id, name, surname, nickname, password, email, addresses
    private val testUser = User(id = "1", name = "Test User", surname = "Test", nickname = "TU", password = "pwd", addresses = listOf(Address(
        street = "123 Main St", code = "T1", number = "123",
        id = null,
        userId = "1",
        complement = null
    )))
    private val testUserOptional: Optional<User> = Optional.of(testUser)
    private val testUserList: List<User> = listOf(testUser)

    @Test
    fun `createUser should delegate to mongoUserService when databaseType is mongo`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "mongo")
        every { mongoUserService.createUser(testUser) } returns testUser
        
        val result = userDatabaseDispatcher.createUser(testUser)
        
        assertEquals(testUser, result)
        verify(exactly = 1) { mongoUserService.createUser(testUser) }
        verify(exactly = 0) { postgresUserService.createUser(any()) }
    }

    @Test
    fun `createUser should delegate to postgresUserService when databaseType is postgres`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "postgres")
        every { postgresUserService.createUser(testUser) } returns testUser
        
        val result = userDatabaseDispatcher.createUser(testUser)
        
        assertEquals(testUser, result)
        verify(exactly = 1) { postgresUserService.createUser(testUser) }
        verify(exactly = 0) { mongoUserService.createUser(any()) }
    }

    @Test
    fun `getAllUsers should delegate to mongoUserService when databaseType is mongo`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "mongo")
        every { mongoUserService.getAllUsers() } returns testUserList
        
        val result = userDatabaseDispatcher.getAllUsers()
        
        assertEquals(testUserList, result)
        verify(exactly = 1) { mongoUserService.getAllUsers() }
        verify(exactly = 0) { postgresUserService.getAllUsers() }
    }

    @Test
    fun `getAllUsers should delegate to postgresUserService when databaseType is postgres`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "postgres")
        every { postgresUserService.getAllUsers() } returns testUserList
        
        val result = userDatabaseDispatcher.getAllUsers()
        
        assertEquals(testUserList, result)
        verify(exactly = 1) { postgresUserService.getAllUsers() }
        verify(exactly = 0) { mongoUserService.getAllUsers() }
    }

    @Test
    fun `getUserById should delegate to mongoUserService when databaseType is mongo`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "mongo")
        val userId = "1"
        every { mongoUserService.getUserById(userId) } returns testUserOptional
        
        val result = userDatabaseDispatcher.getUserById(userId)
        
        assertEquals(testUserOptional, result)
        verify(exactly = 1) { mongoUserService.getUserById(userId) }
        verify(exactly = 0) { postgresUserService.getUserById(any()) }
    }

    @Test
    fun `getUserById should delegate to postgresUserService when databaseType is postgres`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "postgres")
        val userId = "1"
        every { postgresUserService.getUserById(userId) } returns testUserOptional
        
        val result = userDatabaseDispatcher.getUserById(userId)
        
        assertEquals(testUserOptional, result)
        verify(exactly = 1) { postgresUserService.getUserById(userId) }
        verify(exactly = 0) { mongoUserService.getUserById(any()) }
    }

    @Test
    fun `updateUser should delegate to mongoUserService when databaseType is mongo`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "mongo")
        val userId = "1"
        val updatedUser = testUser.copy(name = "Updated Test User")
        every { mongoUserService.updateUser(userId, testUser) } returns updatedUser
        
        val result = userDatabaseDispatcher.updateUser(userId, testUser)
        
        assertEquals(updatedUser, result)
        verify(exactly = 1) { mongoUserService.updateUser(userId, testUser) }
        verify(exactly = 0) { postgresUserService.updateUser(any(), any()) }
    }

    @Test
    fun `updateUser should delegate to postgresUserService when databaseType is postgres`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "postgres")
        val userId = "1"
        val updatedUser = testUser.copy(name = "Updated Test User")
        every { postgresUserService.updateUser(userId, testUser) } returns updatedUser
        
        val result = userDatabaseDispatcher.updateUser(userId, testUser)
        
        assertEquals(updatedUser, result)
        verify(exactly = 1) { postgresUserService.updateUser(userId, testUser) }
        verify(exactly = 0) { mongoUserService.updateUser(any(), any()) }
    }

    @Test
    fun `deleteUser should delegate to mongoUserService when databaseType is mongo`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "mongo")
        val userId = "1"
        every { mongoUserService.deleteUser(userId) } returns Unit
        
        userDatabaseDispatcher.deleteUser(userId)
        
        verify(exactly = 1) { mongoUserService.deleteUser(userId) }
        verify(exactly = 0) { postgresUserService.deleteUser(any()) }
    }

    @Test
    fun `deleteUser should delegate to postgresUserService when databaseType is postgres`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "postgres")
        val userId = "1"
        every { postgresUserService.deleteUser(userId) } returns Unit
        
        userDatabaseDispatcher.deleteUser(userId)
        
        verify(exactly = 1) { postgresUserService.deleteUser(userId) }
        verify(exactly = 0) { mongoUserService.deleteUser(any()) }
    }
    
    @Test
    fun `getUserWithAddressesById should delegate to mongoUserService when databaseType is mongo`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "mongo")
        val userId = "1"
        every { mongoUserService.getUserWithAddressesById(userId) } returns testUserOptional

        val result = userDatabaseDispatcher.getUserWithAddressesById(userId)

        assertEquals(testUserOptional, result)
        verify(exactly = 1) { mongoUserService.getUserWithAddressesById(userId) }
        verify(exactly = 0) { postgresUserService.getUserWithAddressesById(any()) }
    }

    @Test
    fun `getUserWithAddressesById should delegate to postgresUserService when databaseType is postgres`() {
        val userDatabaseDispatcher = UserDatabaseDispatcher(mongoUserService, postgresUserService, "postgres")
        val userId = "1"
        every { postgresUserService.getUserWithAddressesById(userId) } returns testUserOptional

        val result = userDatabaseDispatcher.getUserWithAddressesById(userId)

        assertEquals(testUserOptional, result)
        verify(exactly = 1) { postgresUserService.getUserWithAddressesById(userId) }
        verify(exactly = 0) { mongoUserService.getUserWithAddressesById(any()) }
    }

    //@Test
    fun `dispatcher should throw exception for invalid database type`() {
        // Arrange and Act: Try to create dispatcher with an invalid type
        val invalidType = "oracle"
        
        // Assert
        assertThrows<IllegalStateException> {
            UserDatabaseDispatcher(mongoUserService, postgresUserService, invalidType)
        }
    }
}