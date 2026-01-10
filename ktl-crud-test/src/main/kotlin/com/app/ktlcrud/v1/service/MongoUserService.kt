package com.app.ktlcrud.v1.service

import com.app.ktlcrud.model.User
import com.app.ktlcrud.v1.entity.mapper.toMongoEntity
import com.app.ktlcrud.v1.entity.mapper.toModel
import com.app.ktlcrud.port.UserServicePort
import com.app.ktlcrud.v1.repository.MongoAddressRepository
import com.app.ktlcrud.v1.repository.MongoUserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service("mongoUserService") // Explicitly name the bean to avoid conflict
class MongoUserService(
    private val mongoUserRepository: MongoUserRepository,
    private val mongoAddressRepository: MongoAddressRepository
) : UserServicePort {

    override fun createUser(user: User): User {
        // 1. Convert User model to MongoUserEntity without addressIds for initial save
        var mongoUserEntity = user.toMongoEntity().apply { addressIds = emptyList() }
        val savedMongoUserEntity = mongoUserRepository.save(mongoUserEntity)
        val userId = savedMongoUserEntity.id!!

        // 2. Convert Address models to MongoAddressEntities and save
        val mongoAddressEntities = user.addresses.map { it.toMongoEntity(userId) }
        var savedAddressModels = emptyList<com.app.ktlcrud.model.Address>()
        if (mongoAddressEntities.isNotEmpty()) {
            val savedMongoAddressEntities = mongoAddressRepository.saveAll(mongoAddressEntities)
            savedAddressModels = savedMongoAddressEntities.map { it.toModel() }

            // 3. Update MongoUserEntity with the IDs of the newly saved addresses and save again
            savedMongoUserEntity.addressIds = savedMongoAddressEntities.mapNotNull { it.id }
            mongoUserRepository.save(savedMongoUserEntity)
        }

        return savedMongoUserEntity.toModel().copy(addresses = savedAddressModels)
    }

    override fun getAllUsers(): List<User> {
        return mongoUserRepository.findAll().map { it.toModel() }
    }

    override fun getUserById(id: String): Optional<User> {
        return mongoUserRepository.findById(id).map { it.toModel() }
    }

    override fun updateUser(id: String, user: User): User? {
        return mongoUserRepository.findById(id).map { existingMongoUserEntity ->
            // Update basic user fields
            existingMongoUserEntity.apply {
                name = user.name
                surname = user.surname
                nickname = user.nickname
                password = user.password
            }

            // 1. Delete old addresses
            mongoAddressRepository.deleteByUserId(existingMongoUserEntity.id!!)

            // 2. Create and save new addresses
            val newMongoAddressEntities = user.addresses.map { it.toMongoEntity(existingMongoUserEntity.id!!) }
            var savedAddressModels = emptyList<com.app.ktlcrud.model.Address>()
            if (newMongoAddressEntities.isNotEmpty()) {
                val savedNewMongoAddressEntities = mongoAddressRepository.saveAll(newMongoAddressEntities)
                savedAddressModels = savedNewMongoAddressEntities.map { it.toModel() }
                existingMongoUserEntity.addressIds = savedNewMongoAddressEntities.mapNotNull { it.id }
            } else {
                existingMongoUserEntity.addressIds = emptyList()
            }

            // 3. Update MongoUserEntity and save
            val updatedMongoUserEntity = mongoUserRepository.save(existingMongoUserEntity)

            updatedMongoUserEntity.toModel().copy(addresses = savedAddressModels)
        }.orElse(null)
    }

    override fun deleteUser(id: String) {
        if (mongoUserRepository.existsById(id)) {
            mongoAddressRepository.deleteByUserId(id) // Delete associated addresses
            mongoUserRepository.deleteById(id) // Delete the user
        }
    }

    override fun getUserWithAddressesById(id: String): Optional<User> {
        return mongoUserRepository.findById(id).map { mongoUserEntity ->
            val mongoAddressEntities = mongoAddressRepository.findByUserId(mongoUserEntity.id!!)
            mongoUserEntity.toModel().copy(addresses = mongoAddressEntities.map { it.toModel() })
        }
    }
}