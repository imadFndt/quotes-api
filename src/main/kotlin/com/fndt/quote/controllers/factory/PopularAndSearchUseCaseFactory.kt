package com.fndt.quote.controllers.factory

import com.fndt.quote.domain.QuoteFilter
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.usecases.PopularsUseCase
import com.fndt.quote.domain.usecases.SearchUseCase
import com.fndt.quote.domain.usecases.UseCase

class PopularAndSearchUseCaseFactory(
    private val filterBuilder: QuoteFilter.Builder,
    private val requestManager: RequestManager,
    private val permissionManager: PermissionManager,
) {
    fun getSearchUseCase(query: String, user: User): UseCase<List<Quote>> {
        return SearchUseCase(query, filterBuilder, user, permissionManager, requestManager)
    }

    fun getPopularsUseCase(user: User): UseCase<List<Quote>> {
        return PopularsUseCase(filterBuilder, user, permissionManager, requestManager)
    }
}
