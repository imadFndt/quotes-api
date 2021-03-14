package com.fndt.quote.data

import com.fndt.quote.data.util.toHashed
import com.fndt.quote.data.util.transactionWithIO
import com.fndt.quote.domain.RegistrationService
import com.fndt.quote.domain.dto.AuthRole
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class RegistrationServiceImpl(dbProvider: DatabaseDefinition) : QuotesTablesProvider(dbProvider), RegistrationService {
    override suspend fun registerUser(login: String, password: String) = transactionWithIO {
        usersTable.slice(usersTable.name)
            .select {
                usersTable.name eq login
            }.firstOrNull()?.let {
                return@transactionWithIO false
            }
        usersTable.insert { insert ->
            insert[name] = login
            insert[hashedPassword] = password.toHashed()
            insert[role] = AuthRole.REGULAR
        }
        commit()
        true
    }
}
