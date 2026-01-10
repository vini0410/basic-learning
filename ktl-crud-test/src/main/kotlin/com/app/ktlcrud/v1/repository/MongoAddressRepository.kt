package com.app.ktlcrud.v1.repository

import com.app.ktlcrud.v1.entity.MongoAddressEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoAddressRepository : MongoRepository<MongoAddressEntity, String> {
    fun findByUserId(userId: String): List<MongoAddressEntity>
    fun deleteByUserId(userId: String)
}