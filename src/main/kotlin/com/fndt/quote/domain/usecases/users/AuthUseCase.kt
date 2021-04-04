package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class AuthUseCase(
    private val name: String,
    private val password: String,
    private val userRepository: UserRepository,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<User>(requestManager) {
    override val requestingUser: User? = null

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthAllowed()
    }

    override suspend fun makeRequest(): User {
        val user = userRepository.findUserByParams(name = name, password = password)
        return user ?: throw IllegalStateException("Authentication filed")
    }
}
