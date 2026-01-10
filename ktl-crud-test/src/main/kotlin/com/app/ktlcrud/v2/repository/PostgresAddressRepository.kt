package com.app.ktlcrud.v2.repository

import com.app.ktlcrud.v2.entity.AddressEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PostgresAddressRepository : JpaRepository<AddressEntity, String> {
    fun findByUserId(userId: String): List<AddressEntity>
    fun deleteByUserId(userId: String)
}