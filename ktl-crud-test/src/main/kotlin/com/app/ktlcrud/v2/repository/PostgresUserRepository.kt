package com.app.ktlcrud.v2.repository

import com.app.ktlcrud.v2.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostgresUserRepository : JpaRepository<UserEntity, String>
