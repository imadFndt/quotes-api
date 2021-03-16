package com.fndt.quote.domain.services.implementations

import com.fndt.quote.data.UserDaoImpl
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.services.AuthService
import org.jetbrains.exposed.sql.transactions.transaction

internal class AuthServiceImpl(private val usersDao: UserDaoImpl) : AuthService {
    override suspend fun checkCredentials(login: String, password: String): User? = transaction {
        usersDao.findUser(login, password)
    }
}
