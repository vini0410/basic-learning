package com.app.ktlcrud.v1.repository

import com.app.ktlcrud.v1.entity.MongoUserEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MongoUserRepository : MongoRepository<MongoUserEntity, String>
