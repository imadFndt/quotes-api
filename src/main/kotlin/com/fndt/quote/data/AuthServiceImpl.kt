package com.fndt.quote.data

import com.fndt.quote.data.util.toHashed
import com.fndt.quote.data.util.toUser
import com.fndt.quote.data.util.transactionWithIO
import com.fndt.quote.domain.AuthService
import com.fndt.quote.domain.dto.AuthRole
import io.ktor.auth.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

class AuthServiceImpl(dbProvider: DatabaseDefinition) : QuotesTablesProvider(dbProvider), AuthService {
    override suspend fun checkCredentials(credentials: UserPasswordCredential): AuthRole = transactionWithIO {
        val user = usersTable.select {
            (usersTable.name eq credentials.name) and
                (usersTable.hashedPassword eq credentials.password.toHashed())
        }.firstOrNull()?.toUser()
        user?.role ?: AuthRole.NOT_AUTHORIZED
    }
}
