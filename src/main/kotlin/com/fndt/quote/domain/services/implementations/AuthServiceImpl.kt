package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dao.UserDao
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.services.AuthService

internal class AuthServiceImpl(private val usersDao: UserDao) : AuthService {
    override suspend fun checkCredentials(login: String, password: String): User? {
        return usersDao.findUser(name = login, password = password)
    }
}
