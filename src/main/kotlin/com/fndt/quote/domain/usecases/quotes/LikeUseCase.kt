package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.LikeRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class LikeUseCase(
    private val like: Like,
    private val likeAction: Boolean,
    private val quoteRepository: QuoteRepository,
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<Like>(requestManager) {
    override suspend fun makeRequest(): Like {
        quoteRepository.findById(like.quoteId) ?: throw IllegalArgumentException("Quote does not exist")
        userRepository.findUser(like.userId) ?: throw IllegalArgumentException("User does not exist")
        val likeExists = likeRepository.find(like) != null
        return when {
            likeAction && !likeExists -> likeRepository.like(like)
            !likeAction && likeExists -> likeRepository.unlike(like)
            else -> throw IllegalArgumentException("Like failed")
        } ?: throw IllegalArgumentException("Like failed")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasLikePermission(user)
    }
}
