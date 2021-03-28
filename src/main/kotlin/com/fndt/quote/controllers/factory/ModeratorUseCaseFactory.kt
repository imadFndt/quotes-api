package com.fndt.quote.controllers.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.UseCase
import com.fndt.quote.domain.usecases.moderator.AddTagUseCase
import com.fndt.quote.domain.usecases.moderator.BanUserUseCase
import com.fndt.quote.domain.usecases.moderator.ReviewQuoteUseCase

class ModeratorUseCaseFactory(
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val permissionManager: UserPermissionManager,
    private val requestManager: RequestManager
) {
    fun getAddTagUseCase(tagName: String, requestingUser: User): UseCase<Unit> {
        return AddTagUseCase(tagName, tagRepository, requestingUser, permissionManager, requestManager)
    }

    fun getBanUseCase(quoteId: Int, requestingUser: User): UseCase<Unit> {
        return BanUserUseCase(quoteId, userRepository, requestingUser, permissionManager, requestManager)
    }

    fun getReviewQuoteUseCase(quoteId: Int, decision: Boolean, requestingUser: User): UseCase<Unit> {
        return ReviewQuoteUseCase(quoteId, decision, quoteRepository, requestingUser, permissionManager, requestManager)
    }
}
