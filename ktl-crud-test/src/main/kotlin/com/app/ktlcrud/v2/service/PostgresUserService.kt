package com.app.ktlcrud.v2.service

import com.app.ktlcrud.model.User
import com.app.ktlcrud.port.UserServicePort
import com.app.ktlcrud.v2.entity.mapper.toEntity
import com.app.ktlcrud.v2.entity.mapper.toModel
import com.app.ktlcrud.v2.repository.PostgresAddressRepository
import com.app.ktlcrud.v2.repository.PostgresUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
// ... other imports ...

@Service("postgresUserService")
class PostgresUserService(
    private val postgresUserRepository: PostgresUserRepository,
    private val postgresAddressRepository: PostgresAddressRepository
) : UserServicePort {

    @Transactional
    override fun createUser(user: User): User {
        val userEntity = user.toEntity()
        val savedUserEntity = postgresUserRepository.save(userEntity)

        val addressEntities = user.addresses.map { it.toEntity(savedUserEntity.id) }
        val savedAddressEntities = postgresAddressRepository.saveAll(addressEntities)

        return savedUserEntity.toModel().copy(addresses = savedAddressEntities.map { it.toModel() })
    }

    override fun getAllUsers(): List<User> {
        return postgresUserRepository.findAll().map { it.toModel() }
    }

    override fun getUserById(id: String): Optional<User> {
        return postgresUserRepository.findById(id).map { it.toModel() }
    }

    @Transactional
    override fun updateUser(id: String, user: User): User? {
        return postgresUserRepository.findById(id).map { existingUserEntity ->
            existingUserEntity.apply {
                name = user.name
                surname = user.surname
                nickname = user.nickname
                password = user.password
            }
            val updatedUserEntity = postgresUserRepository.save(existingUserEntity)

            // Delete old addresses and save new ones
            postgresAddressRepository.deleteByUserId(id) // Delete all addresses for this user
            val newAddressEntities = user.addresses.map { it.toEntity(id) }
            val savedNewAddressEntities = postgresAddressRepository.saveAll(newAddressEntities)

            updatedUserEntity.toModel().copy(addresses = savedNewAddressEntities.map { it.toModel() })
        }.orElse(null)
    }

    @Transactional
    override fun deleteUser(id: String) {
        if (postgresUserRepository.existsById(id)) {
            postgresAddressRepository.deleteByUserId(id) // Delete associated addresses
            postgresUserRepository.deleteById(id) // Delete the user
        }
    }

    @Transactional(readOnly = true)
    override fun getUserWithAddressesById(id: String): Optional<User> {
        return postgresUserRepository.findById(id).map { userEntity ->
            val addresses = postgresAddressRepository.findByUserId(id).map { it.toModel() }
            userEntity.toModel().copy(addresses = addresses)
        }
    }
}
