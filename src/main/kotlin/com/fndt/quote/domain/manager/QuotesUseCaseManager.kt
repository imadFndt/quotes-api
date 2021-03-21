package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.usecases.UseCase

interface QuotesUseCaseManager {
    fun getQuotesUseCase(
        search: Int? = null,
        isPublic: Boolean? = null,
        orderPopulars: Boolean = false,
        tagId: Int? = null,
        userRequesting: User? = null,
    ): UseCase<List<Quote>>

    fun addQuotesUseCase(
        body: String,
        userId: Int,
        userRequesting: User? = null,
    ): UseCase<Quote>

    fun likeQuoteUseCase(
        like: Like,
        likeAction: Boolean,
        userRequesting: User? = null,
    ): UseCase<Like>
}
