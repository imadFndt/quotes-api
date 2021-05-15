package com.fndt.quote.rest.factory

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.ProfilePictureManager
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.ban.BanReadOnlyUserUseCase
import com.fndt.quote.domain.usecases.ban.PermanentBanUseCase
import com.fndt.quote.domain.usecases.base.UseCase
import com.fndt.quote.domain.usecases.get.GetUserUseCase
import com.fndt.quote.domain.usecases.users.AuthUseCase
import com.fndt.quote.domain.usecases.users.ChangeProfilePictureUseCase
import com.fndt.quote.domain.usecases.users.ChangeRoleUseCase
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

    fun getBanUseCase(quoteId: Int, requestingUser: User): UseCase<Unit> {
        return BanReadOnlyUserUseCase(quoteId, userRepository, requestingUser, permissionManager, requestManager)
    }

    fun getPermanentBanUseCase(userId: Int, requestingUser: User): UseCase<Unit> {
        return PermanentBanUseCase(userId, userRepository, requestingUser, permissionManager, requestManager)
    }

    fun getChangeRoleUseCase(userId: Int, newRole: AuthRole, requestingUser: User): UseCase<Unit> {
        return ChangeRoleUseCase(userId, newRole, userRepository, requestingUser, permissionManager, requestManager)
    }

    fun getUserUseCase(userId: Int, requestingUser: User): UseCase<User> {
        return GetUserUseCase(userId, userRepository, requestingUser, permissionManager, requestManager)
    }
}
