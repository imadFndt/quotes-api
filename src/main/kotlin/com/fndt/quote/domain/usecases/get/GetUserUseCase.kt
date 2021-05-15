package com.fndt.quote.domain.usecases.get

import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class GetUserUseCase(
    private val userId: Int,
    private val userRepository: UserRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<User>(requestManager) {
    override fun validate(user: User?): Boolean = permissionManager.isAuthorized(requestingUser)

    override suspend fun makeRequest(): User {
        return userRepository.findUserByParams(userId = userId) ?: throw IllegalStateException("User not found")
    }
}
