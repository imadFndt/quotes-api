package com.fndt.quote.rest.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.LikeRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.UseCase
import com.fndt.quote.domain.usecases.quotes.AddQuoteUseCase
import com.fndt.quote.domain.usecases.quotes.LikeUseCase

class QuotesUseCaseFactory(
    private val authorRepository: AuthorRepository,
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val requestManager: RequestManager,
    private val permissionManager: UserPermissionManager,
) {

    fun addQuotesUseCase(body: String, authorName: String, userRequesting: User): UseCase<Unit> {
        return AddQuoteUseCase(
            body,
            authorName,
            quoteRepository,
            authorRepository,
            userRequesting,
            permissionManager,
            requestManager
        )
    }

    fun likeQuoteUseCase(like: Like, likeAction: Boolean, userRequesting: User): UseCase<Unit> {
        return LikeUseCase(
            like,
            likeAction,
            userRequesting,
            quoteRepository,
            userRepository,
            likeRepository,
            permissionManager,
            requestManager
        )
    }
}
