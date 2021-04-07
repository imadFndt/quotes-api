package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class RegisterUseCase(
    private val name: String,
    private val password: String,
    private val userRepository: UserRepository,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<User>(requestManager) {
    override val requestingUser: User? = null

    override fun validate(user: User?): Boolean {
        return permissionManager.isRegisterAllowed()
    }

    override suspend fun makeRequest(): User {
        userRepository.findUserByParams(name = name)?.let { throw IllegalArgumentException("User already registered") }
        val user = User(name = name, password = password)
        val newId = userRepository.add(user)
        return userRepository.findUserByParams(userId = newId) ?: throw IllegalArgumentException("Failed to add user")
    }
}
