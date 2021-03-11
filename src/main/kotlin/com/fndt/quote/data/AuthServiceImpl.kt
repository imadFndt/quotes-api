package com.fndt.quote.data

import com.fndt.quote.domain.AuthService
import com.fndt.quote.domain.dto.AuthRole
import io.ktor.auth.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

class AuthServiceImpl(private val userTable: DbProvider.Users) : AuthService {
    override suspend fun checkCredentials(credentials: UserPasswordCredential): AuthRole = transactionWithIO {
        val user = userTable.select {
            (DbProvider.Users.name eq credentials.name) and
                (DbProvider.Users.hashedPassword eq credentials.password.toHashed())
        }.firstOrNull()?.toUser()
        user?.role ?: AuthRole.NOT_AUTHORIZED
    }
}
