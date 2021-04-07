package com.fndt.quote.domain.manager

import com.fndt.quote.domain.UseCaseAdapter
import com.fndt.quote.domain.dto.*
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.usecases.add.AddQuoteToTagAdapter
import com.fndt.quote.domain.usecases.add.SimpleRepositoryAdapter

class AddAdapterProvider(
    private val repositoryProvider: RepositoryProvider,
    private val permissionManager: UserPermissionManager
) {

    private val isRegularNotBanned: (User?) -> Boolean = { permissionManager.isAuthorized(it) && it?.isBanned == false }

    fun createAddQuoteAdapter(body: String, authorName: String): UseCaseAdapter<Quote> {
        val authorRepository = repositoryProvider.getRepository<AuthorRepository>()
        return SimpleRepositoryAdapter(
            repositoryProvider.getRepository<QuoteRepository>(), isRegularNotBanned
        ) { user ->
            val author = authorRepository.findByName(authorName) ?: run {
                val newId = authorRepository.add(Author(name = authorName))
                authorRepository.findById(newId)
            }
            author ?: return@SimpleRepositoryAdapter null
            Quote(body = body, createdAt = System.currentTimeMillis(), user = user, author = author)
        }
    }

    fun createAddCommentAdapter(body: String, quoteId: Int): UseCaseAdapter<Comment> {
        val repository = repositoryProvider.getRepository<CommentRepository>()
        return SimpleRepositoryAdapter(repository, isRegularNotBanned) { user ->
            repository.findById(quoteId) ?: return@SimpleRepositoryAdapter null
            Comment(body = body, quoteId = quoteId, createdAt = System.currentTimeMillis(), user = user)
        }
    }

    fun createAddTagAdapter(tagName: String): UseCaseAdapter<Tag> {
        return SimpleRepositoryAdapter(
            repositoryProvider.getRepository<TagRepository>(),
            { permissionManager.hasModeratorPermission(it) }
        ) { Tag(name = tagName) }
    }

    fun createAddQuoteToTagAdapter(quoteId: Int, tagId: Int): UseCaseAdapter<Pair<Quote, Tag>> {
        return AddQuoteToTagAdapter(quoteId, tagId, permissionManager, repositoryProvider)
    }
}
