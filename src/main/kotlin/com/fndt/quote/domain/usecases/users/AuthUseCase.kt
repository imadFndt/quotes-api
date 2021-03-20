package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class AuthUseCase(
    private val name: String,
    private val password: String,
    private val userRepository: UserRepository,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<User>(requestManager) {
    override val requestingUser: User? = null

    override suspend fun makeRequest(): User {
        val user = userRepository.findUser(name = name, password = password)
        return user?.also { checkBan(user) } ?: throw IllegalStateException("Authentication filed")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasAuthPermission(requestingUser)
    }

    private fun checkBan(user: User) {
        user.blockedUntil?.let { blockedUntil ->
            if (System.currentTimeMillis() > blockedUntil) userRepository.update(user.id, time = null)
        } ?: run { user }
    }
}
