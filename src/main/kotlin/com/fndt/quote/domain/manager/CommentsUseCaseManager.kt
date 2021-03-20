package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.usecases.UseCase

interface CommentsUseCaseManager {
    fun getCommentsUseCase(quoteId: Int, userRequesting: User? = null): UseCase<List<Comment>>
    fun addCommentsUseCase(
        body: String,
        quoteId: Int,
        userRequesting: User,
    ): UseCase<Comment>
}
