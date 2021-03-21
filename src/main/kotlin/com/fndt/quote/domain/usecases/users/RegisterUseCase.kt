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
        userRepository.findUserByParams(name = name)?.let { throw IllegalArgumentException("User already registered") }
        val user = User(name = name, password = password)
        return userRepository.findUserByParams(userId = userRepository.add(user))
            ?: throw IllegalArgumentException("Failed to add user")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasRegisterPermission(requestingUser)
    }
}
