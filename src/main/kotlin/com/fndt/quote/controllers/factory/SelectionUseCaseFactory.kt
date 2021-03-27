package com.fndt.quote.controllers.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.usecases.PopularsUseCase
import com.fndt.quote.domain.usecases.SearchUseCase
import com.fndt.quote.domain.usecases.TagSelectionUseCase
import com.fndt.quote.domain.usecases.UseCase

class SelectionUseCaseFactory(
    private val quoteRepository: QuoteRepository,
    private val tagRepository: TagRepository,
    private val permissionManager: PermissionManager,
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
}
