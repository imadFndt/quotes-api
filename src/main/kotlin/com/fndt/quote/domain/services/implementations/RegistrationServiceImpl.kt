package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dao.UserDao
import com.fndt.quote.domain.services.RegistrationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class RegistrationServiceImpl(private val userDao: UserDao) : RegistrationService {
    override suspend fun registerUser(login: String, password: String) = withContext(Dispatchers.IO) {
        userDao.findUser(login) ?: throw IllegalArgumentException("User already registered")
        userDao.insert(login, password)
        true
    }
}
