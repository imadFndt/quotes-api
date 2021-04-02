package com.fndt.quote.controllers.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.ProfilePictureManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.UseCase
import com.fndt.quote.domain.usecases.users.AuthUseCase
import com.fndt.quote.domain.usecases.users.ChangeProfilePictureUseCase
import com.fndt.quote.domain.usecases.users.RegisterUseCase
import java.io.File

class UsersUseCaseFactory(
    private val profilePictureManager: ProfilePictureManager,
    private val userRepository: UserRepository,
    private val permissionManager: UserPermissionManager,
    private val requestManager: RequestManager,
) {
    fun authUseCase(name: String, password: String): UseCase<User> {
        return AuthUseCase(name, password, userRepository, permissionManager, requestManager)
    }

    fun registerUseCase(name: String, password: String): UseCase<User> {
        return RegisterUseCase(name, password, userRepository, permissionManager, requestManager)
    }

    fun changeProfilePictureUseCase(profilePicture: File, user: User): UseCase<Unit> {
        return ChangeProfilePictureUseCase(
            profilePicture, user, profilePictureManager, userRepository, permissionManager, requestManager
        )
    }
}
