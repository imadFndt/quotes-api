package com.fndt.quote.rest.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.quotes.SelectionUseCase

class SelectionUseCaseFactory(
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val authorRepository: AuthorRepository,
    private val tagRepository: TagRepository,
    private val permissionManager: UserPermissionManager,
    private val requestManager: RequestManager,
) {
    fun getSelectionsUseCase(arguments: Map<String, Any?>, user: User): SelectionUseCase {
        return SelectionUseCase(
            arguments,
            userRepository,
            authorRepository,
            tagRepository,
            quoteRepository,
            user,
            permissionManager,
            requestManager
        )
    }
}
