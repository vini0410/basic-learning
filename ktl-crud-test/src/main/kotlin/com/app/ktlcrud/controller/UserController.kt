package com.app.ktlcrud.controller

import com.app.ktlcrud.controller.dto.UserRequestDTO
import com.app.ktlcrud.controller.dto.UserResponseDTO
import com.app.ktlcrud.controller.mapper.*
import com.app.ktlcrud.port.UserServicePort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.Optional

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserServicePort) {

    @PostMapping
    fun createUser(@RequestBody userRequest: UserRequestDTO): ResponseEntity<UserResponseDTO> {
        val userModel = userRequest.toModel()
        val userModelResponse = userService.createUser(userModel)
        return ResponseEntity.created(URI.create("/users/${userModelResponse.id}")).body(userModelResponse.toResponseDTO())
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponseDTO>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users.map { it.toResponseDTO() })
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserResponseDTO> {
        return userService.getUserById(id)
            .map { ResponseEntity.ok(it.toResponseDTO()) }
            .orElse(ResponseEntity.notFound().build())
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: String, @RequestBody userRequest: UserRequestDTO): ResponseEntity<UserResponseDTO> {
        val userModel = userRequest.toModel()
        return Optional.ofNullable(userService.updateUser(id, userModel))
            .map { ResponseEntity.ok(it.toResponseDTO()) }
            .orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: String): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/addresses")
    fun getUserWithAddresses(@PathVariable id: String): ResponseEntity<UserResponseDTO> {
        return userService.getUserWithAddressesById(id)
            .map { ResponseEntity.ok(it.toResponseDTO()) }
            .orElse(ResponseEntity.notFound().build())
    }
}
