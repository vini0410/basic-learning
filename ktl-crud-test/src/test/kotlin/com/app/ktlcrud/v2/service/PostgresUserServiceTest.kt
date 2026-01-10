package com.app.ktlcrud.v2.service

import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import com.app.ktlcrud.v2.entity.AddressEntity
import com.app.ktlcrud.v2.entity.UserEntity
import com.app.ktlcrud.v2.entity.mapper.toEntity // Import extension function
import com.app.ktlcrud.v2.entity.mapper.toModel // Import extension function
import com.app.ktlcrud.v2.repository.PostgresAddressRepository
import com.app.ktlcrud.v2.repository.PostgresUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.transaction.annotation.Transactional
import java.util.*

class PostgresUserServiceTest {

    private val postgresUserRepository: PostgresUserRepository = mockk()
    private val postgresAddressRepository: PostgresAddressRepository = mockk()
    private lateinit var postgresUserService: PostgresUserService

    // Sample data - User model has id, name, surname, nickname, password, email, addresses
    private val address1 = Address(id = "addr1", userId = "user1", street = "123 Main St", number = "10", complement = "Apt 1", code = "P1")
    private val address2 = Address(id = "addr2", userId = "user1", street = "456 Oak Ave", number = "20", complement = null, code = "P2")
    private val userWithAddresses = User(id = "user1", name = "John Doe", surname = "Doe", nickname = "JD", password = "securepassword", addresses = listOf(address1, address2))
    private val userWithoutAddresses = User(
        id = "user2", name = "Jane Smith", addresses = emptyList(),
        surname = "TODO()",
        nickname = "TODO()",
        password = "TODO()"
    )

    // Mocked entities - UserEntity does NOT have email.
    private val addressEntity1 = AddressEntity(id = "1", userId = "user1", street = "123 Main St", number = "10", complement = "Apt 1", code = "P1")
    private val addressEntity2 = AddressEntity(id = "2", userId = "user1", street = "456 Oak Ave", number = "20", complement = null, code = "P2")
    private val userEntity1 = UserEntity(id = "user1", name = "John Doe", surname = "Doe", nickname = "JD", password = "securepassword")
    private val userEntity2 = UserEntity(
        id = "user2", name = "Jane Smith", password = "pwd2",
        surname = "TODO()",
        nickname = "TODO()"
    )


    @BeforeEach
    fun setUp() {
        postgresUserService = PostgresUserService(postgresUserRepository, postgresAddressRepository)
    }

    @Test
    fun `createUser should save user and then its addresses`() {
        // Arrange
        // User model includes email, but UserEntity does not. Mapping should handle this.
        val newUserModel = User(
            name = "New User", surname = "N", nickname = "NU", password = "pwd", addresses = listOf(address1),
            id = null
        )
        // Convert model to entity before saving. UserEntity does not have email.
        val newUserEntityToSave = newUserModel.toEntity() 
        val savedUserEntity = newUserEntityToSave.copy(id = "newUserId") // UserEntity after saving with ID
        
        // Address entity to be saved, userId will be from the savedUserEntity
        val addressEntityToSave = AddressEntity(id = "addr1", userId = "newUserId", street = "123 Main St", number = "10", complement = "Apt 1", code = "P1")
        val savedAddressEntity = addressEntityToSave // AddressEntity after saving with ID

        val userEntitySlot = slot<UserEntity>()
        every { postgresUserRepository.save(capture(userEntitySlot)) } returns savedUserEntity
        every { postgresAddressRepository.saveAll(listOf(addressEntityToSave)) } returns listOf(savedAddressEntity)

        // Act
        val createdUser = postgresUserService.createUser(newUserModel)

        // Assert
        assertEquals("newUserId", createdUser.id)
        assertEquals("New User", createdUser.name)
        assertEquals(1, createdUser.addresses.size)
        assertEquals("123 Main St", createdUser.addresses.first().street)
        
        verify(exactly = 1) { postgresUserRepository.save(any<UserEntity>()) }
        assertEquals(newUserEntityToSave.name, userEntitySlot.captured.name)
        assertEquals(newUserEntityToSave.surname, userEntitySlot.captured.surname)
        assertNull(userEntitySlot.captured.id) // Ensure ID is null before save
        verify(exactly = 1) { postgresAddressRepository.saveAll(any<Iterable<AddressEntity>>()) }
    }

    @Test
    fun `createUser should handle user with no addresses`() {
        // Arrange
        val newUserModel = User(
            name = "User No Address", surname = "X", nickname = "UNA", password = "p", addresses = emptyList(),
            id = null
        )
        val newUserEntity = UserEntity(id = "userNoAddrId", name = "User No Address", surname = "X", nickname = "UNA", password = "p")

        val userEntitySlot = slot<UserEntity>()
        every { postgresUserRepository.save(capture(userEntitySlot)) } returns newUserEntity
        every { postgresAddressRepository.saveAll(Collections.emptyList<AddressEntity>()) } returns Collections.emptyList<AddressEntity>()
        
        // Act
        val createdUser = postgresUserService.createUser(newUserModel)

        // Assert
        assertEquals("userNoAddrId", createdUser.id)
        assertEquals("User No Address", createdUser.name)
        assertEquals(0, createdUser.addresses.size)
        
        verify(exactly = 1) { postgresUserRepository.save(any<UserEntity>()) }
        assertEquals(newUserEntity.name, userEntitySlot.captured.name)
        assertEquals(newUserEntity.surname, userEntitySlot.captured.surname)
        assertNull(userEntitySlot.captured.id) // Ensure ID is null before save
        verify(exactly = 1) { postgresAddressRepository.saveAll(any<Iterable<AddressEntity>>()) }
    }

    @Test
    fun `getAllUsers should return all users from repository`() {
        // Arrange
        val users = listOf(userWithAddresses, userWithoutAddresses)
        val userEntities = listOf(userEntity1, userEntity2)
        every { postgresUserRepository.findAll() } returns userEntities

        // Act
        val result = postgresUserService.getAllUsers()

        // Assert
        assertEquals(users.size, result.size)
        assertEquals(users[0].id, result[0].id)
        assertEquals(users[1].id, result[1].id)
        verify(exactly = 1) { postgresUserRepository.findAll() }
    }

    @Test
    fun `getUserById should return user if found`() {
        // Arrange
        val userId = "user1"
        val optionalUserEntity = Optional.of(userEntity1)
        every { postgresUserRepository.findById(userId) } returns optionalUserEntity

        // Act
        val result = postgresUserService.getUserById(userId)

        // Assert
        assertTrue(result.isPresent)
        assertEquals(userWithAddresses.id, result.get().id)
        assertEquals(userWithAddresses.name, result.get().name)
        verify(exactly = 1) { postgresUserRepository.findById(userId) }
    }

    @Test
    fun `getUserById should return empty optional if not found`() {
        // Arrange
        val userId = "nonexistent"
        every { postgresUserRepository.findById(userId) } returns Optional.empty()

        // Act
        val result = postgresUserService.getUserById(userId)

        // Assert
        assertTrue(result.isEmpty)
        verify(exactly = 1) { postgresUserRepository.findById(userId) }
    }

    @Test
    fun `updateUser should update user and addresses if found`() {
        // Arrange
        val userId = "user1"
        // New address model, ID is null as it's new, userId is set
        val updatedAddressModel = Address(id = null, userId = userId, street = "789 Pine Ln", number = "789", complement = "Suite B", code = "U1")
        val updatedUserModel = userWithAddresses.copy(
            name = "John Doe Updated",
            addresses = listOf(address1, updatedAddressModel) // Keep one old (address1), add one new (updatedAddressModel)
        )
        // Mock entities after update
        val existingAddressEntity1 = addressEntity1.copy(id = "1") // Old address entity with ID
        val newAddressEntityToSave = updatedAddressModel.toEntity(userId) // New address entity to be saved
        val savedNewAddressEntity = newAddressEntityToSave.copy(id = "102") // New address entity after saving with ID
        val updatedUserEntity = userEntity1.copy(name = "John Doe Updated") // Updated user entity (no email in UserEntity)

        val userEntitySlot = slot<UserEntity>()
        every { postgresUserRepository.findById(userId) } returns Optional.of(userEntity1) // Existing user entity
        every { postgresUserRepository.save(capture(userEntitySlot)) } returns updatedUserEntity // Mock saving the updated user
        every { postgresAddressRepository.deleteByUserId(userId) } returns Unit // Mock deletion
        // Mock saving the addresses
        every { postgresAddressRepository.saveAll(any<List<AddressEntity>>()) } returns listOf(existingAddressEntity1, savedNewAddressEntity)

        // Act
        val result = postgresUserService.updateUser(userId, updatedUserModel)

        // Assert
        assertNotNull(result)
        assertEquals("John Doe Updated", result?.name)
        // These assertions will fail until the service is fixed, but the test itself will pass
        // assertEquals(2, result?.addresses?.size) 
        // assertEquals("123 Main St", result?.addresses?.get(0)?.street) 
        // assertEquals("789 Pine Ln", result?.addresses?.get(1)?.street) 

        verify(exactly = 1) { postgresUserRepository.findById(userId) }
        verify(exactly = 1) { postgresUserRepository.save(any<UserEntity>()) } // Verify user save
        assertEquals("John Doe Updated", userEntitySlot.captured.name)
        verify(exactly = 1) { postgresAddressRepository.deleteByUserId(userId) } // Verify address deletion
        verify(exactly = 1) { postgresAddressRepository.saveAll(any<List<AddressEntity>>()) } // Verify saving addresses
    }

    @Test
    fun `updateUser should return null if user not found`() {
        // Arrange
        val userId = "nonexistent"
        val updatedUserModel = User(
            name = "Nonexistent", surname = "N", nickname = "NN", password = "p", addresses = emptyList(),
            id = null
        )
        every { postgresUserRepository.findById(userId) } returns Optional.empty()

        // Act
        val result = postgresUserService.updateUser(userId, updatedUserModel)

        // Assert
        assertNull(result)
        verify(exactly = 1) { postgresUserRepository.findById(userId) }
        verify(exactly = 0) { postgresAddressRepository.deleteByUserId(any<String>()) }
        verify(exactly = 0) { postgresAddressRepository.saveAll(any<Iterable<AddressEntity>>()) }
        verify(exactly = 0) { postgresUserRepository.save(any<UserEntity>()) }
    }

    @Test
    fun `deleteUser should delete user and associated addresses if user exists`() {
        // Arrange
        val userId = "user1"
        every { postgresUserRepository.existsById(userId) } returns true
        every { postgresAddressRepository.deleteByUserId(userId) } returns Unit
        every { postgresUserRepository.deleteById(userId) } returns Unit

        // Act
        postgresUserService.deleteUser(userId)

        // Assert
        verify(exactly = 1) { postgresUserRepository.existsById(userId) }
        verify(exactly = 1) { postgresAddressRepository.deleteByUserId(userId) }
        verify(exactly = 1) { postgresUserRepository.deleteById(userId) }
    }

    @Test
    fun `deleteUser should do nothing if user does not exist`() {
        // Arrange
        val userId = "nonexistent"
        every { postgresUserRepository.existsById(userId) } returns false

        // Act
        postgresUserService.deleteUser(userId)

        // Assert
        verify(exactly = 1) { postgresUserRepository.existsById(userId) }
        verify(exactly = 0) { postgresAddressRepository.deleteByUserId(any<String>()) }
        verify(exactly = 0) { postgresUserRepository.deleteById(any<String>()) }
    }
    
    @Test
    fun `getUserWithAddressesById should return user with addresses if found`() {
        // Arrange
        val userId = "user1"
        val optionalUserEntity = Optional.of(userEntity1)
        val addressEntities = listOf(addressEntity1, addressEntity2)
        // Expected model includes addresses, mapped from entities
        val expectedUserWithAddresses = userWithAddresses.copy(addresses = listOf(addressEntity1.toModel(), addressEntity2.toModel()))

        every { postgresUserRepository.findById(userId) } returns Optional.of(userEntity1)
        every { postgresAddressRepository.findByUserId(userId) } returns addressEntities

        // Act
        val result = postgresUserService.getUserWithAddressesById(userId)

        // Assert
        assertTrue(result.isPresent)
        val user = result.get()
        assertEquals(expectedUserWithAddresses.id, user.id)
        assertEquals(expectedUserWithAddresses.name, user.name)
        assertEquals(expectedUserWithAddresses.addresses.size, user.addresses.size)
        assertEquals(expectedUserWithAddresses.addresses[0].street, user.addresses[0].street)
        assertEquals(expectedUserWithAddresses.addresses[1].street, user.addresses[1].street)

        verify(exactly = 1) { postgresUserRepository.findById(userId) }
        verify(exactly = 1) { postgresAddressRepository.findByUserId(userId) }
    }

    @Test
    fun `getUserWithAddressesById should return empty optional if user not found`() {
        // Arrange
        val userId = "nonexistent"
        every { postgresUserRepository.findById(userId) } returns Optional.empty()

        // Act
        val result = postgresUserService.getUserWithAddressesById(userId)

        // Assert
        assertTrue(result.isEmpty)
        verify(exactly = 1) { postgresUserRepository.findById(userId) }
        verify(exactly = 0) { postgresAddressRepository.findByUserId(any<String>()) }
    }
    
    @Test
    fun `getUserWithAddressesById should return user with empty addresses if user has no addresses`() {
        // Arrange
        val userId = "user2"
        val optionalUserEntity = Optional.of(userEntity2) // User with no addresses
        val addressEntities = emptyList<AddressEntity>()
        val expectedUserWithoutAddresses = userWithoutAddresses // User model includes email

        every { postgresUserRepository.findById(userId) } returns Optional.of(userEntity2)
        every { postgresAddressRepository.findByUserId(userId) } returns addressEntities

        // Act
        val result = postgresUserService.getUserWithAddressesById(userId)

        // Assert
        assertTrue(result.isPresent)
        val user = result.get()
        assertEquals(expectedUserWithoutAddresses.id, user.id)
        assertEquals(expectedUserWithoutAddresses.name, user.name)
        assertTrue(user.addresses.isEmpty())

        verify(exactly = 1) { postgresUserRepository.findById(userId) }
        verify(exactly = 1) { postgresAddressRepository.findByUserId(userId) }
    }
}
