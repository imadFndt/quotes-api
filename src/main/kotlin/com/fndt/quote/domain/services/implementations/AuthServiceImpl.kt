package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dao.UserDao
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.services.AuthService

internal class AuthServiceImpl(private val usersDao: UserDao) : AuthService {
    override suspend fun checkCredentials(login: String, password: String): User? {
        val user = usersDao.findUser(name = login, password = password)
        return user?.also {
            user.blockedUntil?.let { blockedUntil ->
                if (System.currentTimeMillis() > blockedUntil) usersDao.update(it.id, time = null)
            } ?: run { user }
        }
    }
}
