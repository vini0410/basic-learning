package com.app.ktlcrud.v1.service

import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import com.app.ktlcrud.v1.entity.MongoAddressEntity
import com.app.ktlcrud.v1.entity.MongoUserEntity
import com.app.ktlcrud.v1.entity.mapper.toMongoEntity // Import extension function
import com.app.ktlcrud.v1.entity.mapper.toModel // Import extension function
import com.app.ktlcrud.v1.repository.MongoAddressRepository
import com.app.ktlcrud.v1.repository.MongoUserRepository
import com.app.ktlcrud.v2.entity.UserEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class MongoUserServiceTest {

    private val mongoUserRepository: MongoUserRepository = mockk()
    private val mongoAddressRepository: MongoAddressRepository = mockk()
    private lateinit var mongoUserService: MongoUserService

    // Sample data - User model has id, name, surname, nickname, password, email, addresses
    private val address1 = Address(id = "addr1", userId = "user1", street = "123 Main St", number = "10", complement = "Apt 1", code = "A1")
    private val address2 = Address(id = "addr2", userId = "user1", street = "456 Oak Ave", number = "20", complement = null, code = "A2")
    private val userWithAddresses = User(id = "user1", name = "John Doe", surname = "Doe", nickname = "JD", password = "securepassword", addresses = listOf(address1, address2))
    private val userWithoutAddresses = User(
        id = "user2", name = "Jane Smith", addresses = emptyList(),
        surname = "",
        nickname = "",
        password = ""
    )

    // Mocked entities - MongoUserEntity does NOT have email.
    private val mongoUserEntity1 = MongoUserEntity(id = "user1", name = "John Doe", surname = "Doe", nickname = "JD", password = "securepassword", addressIds = listOf("addr1", "addr2"))
    private val mongoUserEntity2 = MongoUserEntity(
        id = "user2", name = "Jane Smith", addressIds = emptyList(),
        surname = "",
        nickname = "",
        password = ""
    )
    // MongoAddressEntity has id, userId, code, street, number, complement
    private val mongoAddressEntity1 = MongoAddressEntity(id = "addr1", userId = "user1", street = "123 Main St", number = "10", complement = "Apt 1", code = "A1")
    private val mongoAddressEntity2 = MongoAddressEntity(id = "addr2", userId = "user1", street = "456 Oak Ave", number = "20", complement = null, code = "A2")


    @BeforeEach
    fun setUp() {
        mongoUserService = MongoUserService(mongoUserRepository, mongoAddressRepository)
    }

    @Test
    fun `createUser should save user and addresses, then update user with address IDs`() {
        // Arrange
        // User model includes email, but MongoUserEntity does not. Mapping should handle this.
        val newUserModel = User(
            name = "New User",
            surname = "N",
            nickname = "NU",
            password = "pwd",
            addresses = listOf(Address(
                street = "123 Main St", code = "R1", number = "123",
                id = "",
                userId = "",
                complement = null
            )),
            id = null
        )
        
        // Mock the save operations
        val savedUserEntityWithoutIds = MongoUserEntity(id = "newUserId", name = "New User", surname = "N", nickname = "NU", password = "pwd", addressIds = emptyList())
        val newAddressEntityToSave = MongoAddressEntity(
            userId = "newUserId", street = "123 Main St", number = "123", code = "R1",
            id = null,
            complement = null
        )
        val savedNewAddressEntity = newAddressEntityToSave.copy(id = "newAddressId")
        val finalSavedUserEntity = savedUserEntityWithoutIds.copy(addressIds = listOf("newAddressId"))

        val userEntitySlot = slot<MongoUserEntity>()
        // Mock the sequence of saves: first the user, then addresses, then user again with IDs.
        every { mongoUserRepository.save(capture(userEntitySlot)) } returns savedUserEntityWithoutIds andThen finalSavedUserEntity
        every { mongoAddressRepository.saveAll(any<Iterable<MongoAddressEntity>>()) } returns listOf(savedNewAddressEntity)

        // Act
        val createdUser = mongoUserService.createUser(newUserModel)

        // Assert
        assertEquals("newUserId", createdUser.id)
        assertEquals("New User", createdUser.name)
        assertEquals(1, createdUser.addresses.size)
        assertEquals("123 Main St", createdUser.addresses.first().street)

        // Verify user save calls
        verify(exactly = 2) { mongoUserRepository.save(any<MongoUserEntity>()) }
        // Assert on captured values
        assertTrue(userEntitySlot.captured.addressIds.isNotEmpty())
        assertEquals(1, userEntitySlot.captured.addressIds.size)
        assertEquals("newAddressId", userEntitySlot.captured.addressIds.first())
        
        verify(exactly = 1) { mongoAddressRepository.saveAll(any<Iterable<MongoAddressEntity>>()) }
    }
    
    @Test
    fun `createUser should handle user with no addresses`() {
        // Arrange
        val newUserModel = User(
            name = "User No Address", surname = "X", nickname = "UNA", password = "p", addresses = emptyList(),
            id = null
        )
        val savedUserEntity = MongoUserEntity(id = "userNoAddrId", name = "User No Address", surname = "X", nickname = "UNA", password = "p", addressIds = emptyList())

        every { mongoUserRepository.save(ofType(MongoUserEntity::class)) } returns savedUserEntity
        
        // Act
        val createdUser = mongoUserService.createUser(newUserModel)

        // Assert
        assertEquals("userNoAddrId", createdUser.id)
        assertEquals("User No Address", createdUser.name)
        assertEquals(0, createdUser.addresses.size)
        
        verify(exactly = 1) { mongoUserRepository.save(ofType(MongoUserEntity::class)) }
        verify(exactly = 0) { mongoAddressRepository.saveAll(any<Iterable<MongoAddressEntity>>()) }
    }


    @Test
    fun `getAllUsers should return all users from repository`() {
        // Arrange
        val users = listOf(userWithAddresses, userWithoutAddresses)
        val mongoUsers = listOf(mongoUserEntity1, mongoUserEntity2)
        every { mongoUserRepository.findAll() } returns mongoUsers

        // Act
        val result = mongoUserService.getAllUsers()

        // Assert
        assertEquals(users.size, result.size)
        assertEquals(users[0].id, result[0].id)
        assertEquals(users[1].id, result[1].id)
        verify(exactly = 1) { mongoUserRepository.findAll() }
    }

    @Test
    fun `getUserById should return user if found`() {
        // Arrange
        val userId = "user1"
        val optionalUserEntity = Optional.of(mongoUserEntity1)
        every { mongoUserRepository.findById(userId) } returns optionalUserEntity

        // Act
        val result = mongoUserService.getUserById(userId)

        // Assert
        assertTrue(result.isPresent)
        assertEquals(userWithAddresses.id, result.get().id)
        assertEquals(userWithAddresses.name, result.get().name)
        verify(exactly = 1) { mongoUserRepository.findById(userId) }
    }

    @Test
    fun `getUserById should return empty optional if not found`() {
        // Arrange
        val userId = "nonexistent"
        every { mongoUserRepository.findById(userId) } returns Optional.empty()

        // Act
        val result = mongoUserService.getUserById(userId)

        // Assert
        assertTrue(result.isEmpty)
        verify(exactly = 1) { mongoUserRepository.findById(userId) }
    }

    @Test
    fun `updateUser should update user and addresses if found`() {
        // Arrange
        val userId = "user1"
        // Updated address model - new ID is null as it's new, userId is set
        val updatedAddress = Address(id = null, userId = userId, street = "789 Pine Ln", number = "789", complement = "Suite B", code = "U1")
        val updatedUserModel = userWithAddresses.copy(
            name = "John Doe Updated",
            addresses = listOf(address1, updatedAddress) // Keep one old (address1), add one new (updatedAddress)
        )
        // Mock entities after update
        val existingAddressEntity1 = mongoAddressEntity1.copy(id = "addr1") // Old address entity remains
        val newAddressEntityToSave = MongoAddressEntity(userId = userId, street = "789 Pine Ln", number = "789", complement = "Suite B", code = "U1") // New address entity without ID yet
        val savedNewAddressEntity = newAddressEntityToSave.copy(id = "addr3") // New address entity after saving with ID
        val updatedUserEntity = MongoUserEntity(id = userId, name = "John Doe Updated", surname = "Doe", nickname = "JD", password = "securepassword", addressIds = listOf("addr1", "addr3")) // Updated user entity (no email)

        val addressSlot = slot<List<MongoAddressEntity>>()
        every { mongoUserRepository.findById(userId) } returns Optional.of(mongoUserEntity1) // Existing user entity
        every { mongoAddressRepository.deleteByUserId(userId) } returns Unit // Mocking deletion
        // Mock saving the addresses
        every { mongoAddressRepository.saveAll(capture(addressSlot)) } returns listOf(existingAddressEntity1.copy(id="addr1"), savedNewAddressEntity)
        every { mongoUserRepository.save(any<MongoUserEntity>()) } returns updatedUserEntity // Mocking the final save of user entity

        // Act
        val result = mongoUserService.updateUser(userId, updatedUserModel)

        // Assert
        assertNotNull(result)
        assertEquals("John Doe Updated", result?.name)
        assertEquals(2, result?.addresses?.size)
        assertEquals("addr1", result?.addresses?.find { it.street == "123 Main St" }?.id)
        assertEquals("addr3", result?.addresses?.find { it.street == "789 Pine Ln" }?.id)


        verify(exactly = 1) { mongoUserRepository.findById(userId) }
        verify(exactly = 1) { mongoAddressRepository.deleteByUserId(userId) }
        // Verify saveAll was called and assert on the captured list
        verify(exactly = 1) { mongoAddressRepository.saveAll(any<List<MongoAddressEntity>>()) }
        assertEquals(2, addressSlot.captured.size)
        assertTrue(addressSlot.captured.any { it.street == "123 Main St" && it.id == "addr1" })
        assertTrue(addressSlot.captured.any { it.street == "789 Pine Ln" && it.id == null })
        verify(exactly = 1) { mongoUserRepository.save(any<MongoUserEntity>()) } // Check final user save
    }

    @Test
    fun `updateUser should return null if user not found`() {
        // Arrange
        val userId = "nonexistent"
        val updatedUserModel = User(
            name = "Nonexistent", surname = "N", nickname = "NN", password = "p", addresses = emptyList(),
            id = null
        )
        every { mongoUserRepository.findById(userId) } returns Optional.empty()

        // Act
        val result = mongoUserService.updateUser(userId, updatedUserModel)

        // Assert
        assertNull(result)
        verify(exactly = 1) { mongoUserRepository.findById(userId) }
        verify(exactly = 0) { mongoAddressRepository.deleteByUserId(any<String>()) }
        verify(exactly = 0) { mongoAddressRepository.saveAll(any<Iterable<MongoAddressEntity>>()) }
        verify(exactly = 0) { mongoUserRepository.save(any<MongoUserEntity>()) }
    }

    @Test
    fun `deleteUser should delete user and associated addresses if user exists`() {
        // Arrange
        val userId = "user1"
        every { mongoUserRepository.existsById(userId) } returns true
        every { mongoAddressRepository.deleteByUserId(userId) } returns Unit
        every { mongoUserRepository.deleteById(userId) } returns Unit

        // Act
        mongoUserService.deleteUser(userId)

        // Assert
        verify(exactly = 1) { mongoUserRepository.existsById(userId) }
        verify(exactly = 1) { mongoAddressRepository.deleteByUserId(userId) }
        verify(exactly = 1) { mongoUserRepository.deleteById(userId) }
    }

    @Test
    fun `deleteUser should do nothing if user does not exist`() {
        // Arrange
        val userId = "nonexistent"
        every { mongoUserRepository.existsById(userId) } returns false

        // Act
        mongoUserService.deleteUser(userId)

        // Assert
        verify(exactly = 1) { mongoUserRepository.existsById(userId) }
        verify(exactly = 0) { mongoAddressRepository.deleteByUserId(any<String>()) }
        verify(exactly = 0) { mongoUserRepository.deleteById(any<String>()) }
    }
    
    @Test
    fun `getUserWithAddressesById should return user with addresses if found`() {
        // Arrange
        val userId = "user1"
        val optionalUserEntity = Optional.of(mongoUserEntity1)
        val addressEntities = listOf(mongoAddressEntity1, mongoAddressEntity2)
        // Expected model includes addresses, mapped from entities
        val expectedUserWithAddresses = userWithAddresses.copy(addresses = listOf(mongoAddressEntity1.toModel(), mongoAddressEntity2.toModel()))

        every { mongoUserRepository.findById(userId) } returns Optional.of(mongoUserEntity1)
        every { mongoAddressRepository.findByUserId(userId) } returns addressEntities

        // Act
        val result = mongoUserService.getUserWithAddressesById(userId)

        // Assert
        assertTrue(result.isPresent)
        val user = result.get()
        assertEquals(expectedUserWithAddresses.id, user.id)
        assertEquals(expectedUserWithAddresses.name, user.name)
        assertEquals(expectedUserWithAddresses.addresses.size, user.addresses.size)
        assertEquals(expectedUserWithAddresses.addresses[0].street, user.addresses[0].street)
        assertEquals(expectedUserWithAddresses.addresses[1].street, user.addresses[1].street)

        verify(exactly = 1) { mongoUserRepository.findById(userId) }
        verify(exactly = 1) { mongoAddressRepository.findByUserId(userId) }
    }

    @Test
    fun `getUserWithAddressesById should return empty optional if user not found`() {
        // Arrange
        val userId = "nonexistent"
        every { mongoUserRepository.findById(userId) } returns Optional.empty()

        // Act
        val result = mongoUserService.getUserWithAddressesById(userId)

        // Assert
        assertTrue(result.isEmpty)
        verify(exactly = 1) { mongoUserRepository.findById(userId) }
        verify(exactly = 0) { mongoAddressRepository.findByUserId(any<String>()) }
    }
    
    @Test
    fun `getUserWithAddressesById should return user with empty addresses if user has no addresses`() {
        // Arrange
        val userId = "user2"
        val optionalUserEntity = Optional.of(mongoUserEntity2) // User with no addresses
        val addressEntities = emptyList<MongoAddressEntity>()
        val expectedUserWithoutAddresses = userWithoutAddresses // Ensure email is included in expected model

        every { mongoUserRepository.findById(userId) } returns Optional.of(mongoUserEntity2)
        every { mongoAddressRepository.findByUserId(userId) } returns addressEntities

        // Act
        val result = mongoUserService.getUserWithAddressesById(userId)

        // Assert
        assertTrue(result.isPresent)
        val user = result.get()
        assertEquals(expectedUserWithoutAddresses.id, user.id)
        assertEquals(expectedUserWithoutAddresses.name, user.name)
        assertTrue(user.addresses.isEmpty())

        verify(exactly = 1) { mongoUserRepository.findById(userId) }
        verify(exactly = 1) { mongoAddressRepository.findByUserId(userId) }
    }
}
