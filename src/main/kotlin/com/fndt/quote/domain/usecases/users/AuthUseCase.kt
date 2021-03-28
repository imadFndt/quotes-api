package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class AuthUseCase(
    private val name: String,
    private val password: String,
    private val userRepository: UserRepository,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<User>(requestManager) {
    override val requestingUser: User? = null

    override suspend fun makeRequest(): User {
        val user = userRepository.findUserByParams(name = name, password = password)
        return user?.let { checkBan(user) } ?: throw IllegalStateException("Authentication filed")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser)
    }

    private fun checkBan(user: User): User? {
        user.blockedUntil?.let { blockedUntil ->
            return if (System.currentTimeMillis() > blockedUntil) {
                userRepository.update(user.id, time = null)
                userRepository.findUserByParams(userId = user.id)
            } else {
                null
            }
        }
        return user
    }
}
