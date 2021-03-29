package com.fndt.quote.controllers.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.UseCase
import com.fndt.quote.domain.usecases.selections.*

class SelectionUseCaseFactory(
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val authorRepository: AuthorRepository,
    private val tagRepository: TagRepository,
    private val permissionManager: UserPermissionManager,
    private val requestManager: RequestManager,
) {
    fun getSearchUseCase(query: String, user: User): UseCase<List<Quote>> {
        return SearchUseCase(query, quoteRepository, user, permissionManager, requestManager)
    }

    fun getPopularsUseCase(user: User): UseCase<List<Quote>> {
        return PopularsUseCase(quoteRepository, user, permissionManager, requestManager)
    }

    fun getTagSelectionUseCase(tagId: Int, user: User): TagSelectionUseCase {
        return TagSelectionUseCase(tagId, quoteRepository, tagRepository, user, permissionManager, requestManager)
    }

    fun getAuthorSelectionUseCase(authorId: Int, user: User): AuthorSelectionUseCase {
        return AuthorSelectionUseCase(
            authorId, quoteRepository, authorRepository, user, permissionManager, requestManager
        )
    }

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
