package com.app.ktlcrud.controller

import com.app.ktlcrud.controller.dto.AddressDTO
import com.app.ktlcrud.controller.dto.UserRequestDTO
import com.app.ktlcrud.controller.mapper.toModel
import com.app.ktlcrud.controller.mapper.toResponseDTO
import com.app.ktlcrud.model.Address
import com.app.ktlcrud.model.User
import com.app.ktlcrud.port.UserServicePort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.Optional

class UserControllerTest {

    private val userService: UserServicePort = mockk()
    // Mappings are extension functions, so no direct mapper instance is needed here.

    // UserController constructor likely takes UserServicePort
    private val userController = UserController(userService)

    @Test
    fun `getAllUsers should return all users`() {
        // Arrange
        val users = listOf(
            User(
                id = "1", name = "John Doe", surname = "Doe", nickname = "JD", password = "pwd", addresses = listOf(
                    Address(
                        street = "123 Main St", code = "C1", number = "123",
                        id = null,
                        userId = "1",
                        complement = null
                    )
                )
            ),
            User(
                id = "2",
                name = "Jane Smith",
                surname = "Smith",
                nickname = "JS",
                password = "pwd2",
                addresses = listOf()
            )
        )
        // Use extension functions directly on models
        val userResponseDTOs = users.map { it.toResponseDTO() }
        every { userService.getAllUsers() } returns users

        // Act
        val response = userController.getAllUsers()

        // Assert
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == userResponseDTOs)
    }

    @Test
    fun `getUserById should return user when found`() {
        // Arrange
        val userId = "1"
        val user = User(
            id = userId,
            name = "John Doe",
            surname = "Doe",
            nickname = "JD",
            password = "pwd",
            addresses = listOf(Address(
                street = "123 Main St", code = "C1", number = "123",
                id = null,
                userId = "1",
                complement = null
            ))
        )
        // Use extension functions directly on models
        val userResponseDTO = user.toResponseDTO()
        // Assuming service returns User? or User
        every { userService.getUserById(userId) } returns Optional.of<User>(user)

        // Act
        val response = userController.getUserById(userId)

        // Assert
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == userResponseDTO)
    }

    @Test
    fun `getUserById should return not found when user not found`() {
        // Arrange
        val userId = "nonexistent_id"
        // Assuming service returns User? or null
        every { userService.getUserById(userId) } returns Optional.empty<User>()

        // Act
        val response = userController.getUserById(userId)

        // Assert
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body == null)
    }

    @Test
    fun `createUser should return created user`() {
        // Arrange
        // UserRequestDTO does NOT have an email field.
        val userRequestDTO = UserRequestDTO(
            name = "New User",
            surname = "Doe",
            nickname = "ND",
            password = "securepassword",
            addresses = listOf(AddressDTO(
                street = "456 Oak Ave", number = "456", code = "R1",
                complement = null
            ))
        )
        val newUserModel = userRequestDTO.toModel() // Map DTO to Model
        // Assume the service returns a User with an ID after saving. User model HAS email.
        val savedUserWithId = newUserModel.copy(id = "3")
        val userResponseDTO = savedUserWithId.toResponseDTO() // Map Model to Response DTO

        // Mock userService.saveUser to return the saved user with ID
        every { userService.createUser(any()) } returns savedUserWithId

        // Act
        val response = userController.createUser(userRequestDTO)

        // Assert
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body == userResponseDTO)
        // Verify that the userService was called with the correctly mapped user model
        verify(exactly = 1) { userService.createUser(any()) }
    }

    @Test
    fun `updateUser should return updated user`() {
        // Arrange
        val userId = "1"
        // Construct UserRequestDTO with all expected parameters (even if null)
        val userUpdateRequestDTO = UserRequestDTO(
            name = "John Doe Updated",
            surname = "Doe",
            nickname = "JD Updated",
            password = "newsecurepassword",
            addresses = listOf(
                AddressDTO(
                    street = "123 Main St Updated",
                    number = "123",
                    code = "U1",
                    complement = null
                )
            )
        )
        // Map DTO to model. User model has email.
        val updatedUserModel = userUpdateRequestDTO.toModel()
        // Assume the service returns the updated user with ID. User model has email.
        val updatedUserWithId = updatedUserModel.copy(id = userId)
        val userResponseDTO = updatedUserWithId.toResponseDTO()

        // Mock userService.updateUser to return the updated user
        every { userService.updateUser(userId, any()) } returns updatedUserWithId

        // Act
        val response = userController.updateUser(userId, userUpdateRequestDTO)

        // Assert
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == userResponseDTO)
        // Verify that the userService was called with the correct ID and mapped user model
        verify(exactly = 1) { userService.updateUser(userId, any()) }
    }

    @Test
    fun `updateUser should return not found when user not found`() {
        // Arrange
        val userId = "nonexistent_id"
        val userUpdateRequestDTO = UserRequestDTO(
            name = "Nonexistent User",
            surname = "Nonexistent",
            nickname = "NN",
            password = "pwd",
            addresses = listOf(AddressDTO(
                street = "1 Street", number = "1", code = "N1",
                complement = null
            ))
        )
        every { userService.updateUser(userId, any()) } returns null

        // Act
        val response = userController.updateUser(userId, userUpdateRequestDTO)

        // Assert
        assert(response.statusCode == HttpStatus.NOT_FOUND)
        assert(response.body == null)
    }

    @Test
    fun `deleteUser should return no content when deleted`() {
        // Arrange
        val userId = "1"
        every { userService.deleteUser(userId) } returns Unit

        // Act
        val response = userController.deleteUser(userId)

        // Assert
        assert(response.statusCode == HttpStatus.NO_CONTENT)
        assert(response.body == null)
        verify(exactly = 1) { userService.deleteUser(userId) }
    }

    @Test
    fun `deleteUser should also return no content when user not found`() {
        // Arrange
        val userId = "nonexistent_id"
        every { userService.deleteUser(userId) } returns Unit

        // Act
        val response = userController.deleteUser(userId)

        // Assert
        assert(response.statusCode == HttpStatus.NO_CONTENT)
        assert(response.body == null)
        verify(exactly = 1) { userService.deleteUser(userId) }
    }
}
