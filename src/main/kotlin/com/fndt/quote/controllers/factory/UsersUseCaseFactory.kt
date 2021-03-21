package com.fndt.quote.controllers.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.manager.UsersUseCaseManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.UseCase
import com.fndt.quote.domain.usecases.users.AuthUseCase
import com.fndt.quote.domain.usecases.users.RegisterUseCase

class UsersUseCaseFactory(
    private val userRepository: UserRepository,
    private val permissionManager: PermissionManager,
    private val requestManager: RequestManager,
) : UsersUseCaseManager {
    override fun authUseCase(name: String, password: String): UseCase<User> {
        return AuthUseCase(name, password, userRepository, permissionManager, requestManager)
    }

    override fun registerUseCase(name: String, password: String): UseCase<User> {
        return RegisterUseCase(name, password, userRepository, permissionManager, requestManager)
    }
}
