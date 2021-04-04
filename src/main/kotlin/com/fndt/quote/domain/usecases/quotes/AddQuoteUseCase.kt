package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.dto.isBanned
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class AddQuoteUseCase(
    private val body: String,
    private val authorName: String,
    private val quoteRepository: QuoteRepository,
    private val authorRepository: AuthorRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(user) && user?.isBanned == false
    }

    override suspend fun makeRequest() {
        val author = authorRepository.findByName(authorName) ?: run {
            val newId = authorRepository.add(Author(name = authorName))
            authorRepository.findById(newId) ?: throw IllegalStateException("failed to find author")
        }
        val quote = Quote(body = body, createdAt = System.currentTimeMillis(), user = requestingUser, author = author)
        quoteRepository.add(quote)
    }
}
