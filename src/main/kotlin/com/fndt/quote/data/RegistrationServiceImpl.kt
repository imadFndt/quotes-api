package com.fndt.quote.data

import com.fndt.quote.domain.RegistrationService
import com.fndt.quote.domain.dto.AuthRole
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class RegistrationServiceImpl(private val userTable: DbProvider.Users) : RegistrationService {
    override suspend fun registerUser(login: String, password: String) = transactionWithIO {
        val user = userTable.slice(userTable.name).select { userTable.name eq login }.firstOrNull()
        user?.let { return@transactionWithIO false }
        userTable.insert { insertStatement ->
            insertStatement[name] = login
            insertStatement[hashedPassword] = password.toHashed()
            insertStatement[role] = AuthRole.REGULAR.byte
        }
        commit()
        true
    }
}
