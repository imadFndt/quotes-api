package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class RegisterUseCase(
    private val name: String,
    private val password: String,
    private val userRepository: UserRepository,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<User>(requestManager) {
    override val requestingUser: User? = null

    override suspend fun makeRequest(): User {
        userRepository.findUser(name = name)?.let { throw IllegalArgumentException("User already registered") }
        return userRepository.insert(name, password) ?: throw IllegalStateException("Failed to register")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasRegisterPermission(requestingUser)
    }
}
