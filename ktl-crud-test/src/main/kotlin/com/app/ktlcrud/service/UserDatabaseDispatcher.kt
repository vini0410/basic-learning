package com.app.ktlcrud.service

import com.app.ktlcrud.model.User
import com.app.ktlcrud.port.UserServicePort
import com.app.ktlcrud.v1.service.MongoUserService
import com.app.ktlcrud.v2.service.PostgresUserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.util.*

@Service
@Primary
class UserDatabaseDispatcher(
    private val mongoUserService: MongoUserService,
    private val postgresUserService: PostgresUserService,
    @Value("\${app.database.type}") private val databaseType: String
) : UserServicePort {

    private val delegate: UserServicePort by lazy {
        when (databaseType) {
            "mongo" -> mongoUserService
            "postgres" -> postgresUserService
            else -> throw IllegalStateException("Invalid database type specified: $databaseType. Must be 'mongo' or 'postgres'.")
        }
    }

    override fun createUser(user: User): User = delegate.createUser(user)

    override fun getAllUsers(): List<User> = delegate.getAllUsers()

    override fun getUserById(id: String): Optional<User> = delegate.getUserById(id)

    override fun updateUser(id: String, user: User): User? = delegate.updateUser(id, user)

    override fun deleteUser(id: String) = delegate.deleteUser(id)

    override fun getUserWithAddressesById(id: String): Optional<User> = delegate.getUserWithAddressesById(id)
}
