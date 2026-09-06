package com.study.NullObjectApp.repository

import com.study.NullObjectApp.domain.user.NullUser
import com.study.NullObjectApp.domain.user.RegisteredUser
import com.study.NullObjectApp.domain.user.User
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class UserRepository {

    private val database = ConcurrentHashMap<String, RegisteredUser>(
        mapOf(
            "U-100" to RegisteredUser("U-100", "Alice Admin", "alice@company.com", "ADMIN", 0.15, setOf("READ", "WRITE", "DELETE")),
            "U-200" to RegisteredUser("U-200", "Bob User", "bob@company.com", "USER", 0.05, setOf("READ"))
        )
    )

    /**
     * NUNCA retorna null. Se nao encontrar o usuario, retorna o NullUser.
     */
    fun findById(id: String?): User {
        if (id.isNullOrBlank()) return NullUser()
        return database[id] ?: NullUser()
    }
}
