package com.app.ktlcrud.port

import com.app.ktlcrud.model.User
import java.util.Optional

interface UserServicePort {
    fun createUser(user: User): User
    fun getAllUsers(): List<User>
    fun getUserById(id: String): Optional<User>
    fun updateUser(id: String, user: User): User?
    fun deleteUser(id: String)
    fun getUserWithAddressesById(id: String): Optional<User>
}