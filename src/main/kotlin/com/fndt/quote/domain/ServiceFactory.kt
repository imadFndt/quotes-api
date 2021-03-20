package com.fndt.quote.domain

import com.fndt.quote.data.RequestManagerImpl
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.repository.*
import com.fndt.quote.domain.services.AuthService
import com.fndt.quote.domain.services.RegistrationService
import com.fndt.quote.domain.services.RegularUserService
import com.fndt.quote.domain.services.implementations.*

class ServiceFactory(
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val quoteRepository: QuoteRepository,
    private val tagRepository: TagRepository,
    private val requestManager: RequestManager,
) {

    @Suppress("UNCHECKED_CAST")
    fun createUserService(role: AuthRole?): RegularUserService? {
        return when (role) {
            AuthRole.ADMIN -> AdminUserServiceImpl(
                userRepository,
                commentRepository,
                quoteRepository,
                likeRepository,
                requestManager,
                tagRepository,
            )
            AuthRole.MODERATOR -> ModeratorUserServiceImpl(
                userRepository,
                commentRepository,
                quoteRepository,
                likeRepository,
                tagRepository,
                requestManager,
            )
            AuthRole.REGULAR -> RegularUserServiceImpl(
                commentRepository, quoteRepository, likeRepository, tagRepository, userRepository,
                RequestManagerImpl()
            )
            else -> null
        }
    }

    fun createAuthService(): AuthService = AuthServiceImpl(userRepository)
    fun createRegistrationService(): RegistrationService = RegistrationServiceImpl(userRepository)
}
