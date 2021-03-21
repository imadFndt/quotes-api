package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class BanUser(
    private val userId: Int,
    private val time: Int?,
    private val userRepository: UserRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<User>(requestManager) {
    override suspend fun makeRequest(): User {
        userRepository.findUserByParams(userId) ?: throw IllegalStateException("User not found")
        return userRepository.update(
            time = time?.let { System.currentTimeMillis() + it } ?: run { null },
            userId = userId
        ) ?: throw IllegalStateException("Update failed")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasBanUserPermission(user)
    }
}
