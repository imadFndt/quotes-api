package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.services.AuthService

internal class AuthServiceImpl(private val usersRepository: UserRepository) : AuthService {
    override suspend fun authenticate(login: String, password: String): User? {
        val user = usersRepository.findUser(name = login, password = password)
        return user?.also {
            user.blockedUntil?.let { blockedUntil ->
                if (System.currentTimeMillis() > blockedUntil) usersRepository.update(it.id, time = null)
            } ?: run { user }
        }
    }
}
