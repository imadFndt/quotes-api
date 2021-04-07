package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.RepositoryProvider
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.manager.getRepository
import com.fndt.quote.domain.repository.LikeRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class LikeUseCase(
    private val like: Like,
    private val likeAction: Boolean,
    override val requestingUser: User,
    repositoryProvider: RepositoryProvider,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<Unit>(requestManager) {

    private val quoteRepository = repositoryProvider.getRepository<QuoteRepository>()
    private val userRepository = repositoryProvider.getRepository<UserRepository>()
    private val likeRepository = repositoryProvider.getRepository<LikeRepository>()

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(user)
    }

    override suspend fun makeRequest() {
        quoteRepository.findById(like.quoteId) ?: throw IllegalArgumentException("Quote does not exist")
        userRepository.findUserByParams(like.userId) ?: throw IllegalArgumentException("User does not exist")
        val likeExists = likeRepository.find(like) != null
        when {
            likeAction && !likeExists -> likeRepository.add(like)
            !likeAction && likeExists -> likeRepository.remove(like)
            else -> throw IllegalArgumentException("Like failed")
        }
    }
}
