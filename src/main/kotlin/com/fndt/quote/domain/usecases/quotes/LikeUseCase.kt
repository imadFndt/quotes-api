package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.LikeRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class LikeUseCase(
    private val like: Like,
    private val likeAction: Boolean,
    override val requestingUser: User,
    private val quoteRepository: QuoteRepository,
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<Unit>(requestManager) {

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
