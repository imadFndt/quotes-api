package com.fndt.quote.domain.usecases.add

import com.fndt.quote.domain.UseCaseAdapter
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.RepositoryProvider
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.manager.getRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.TagSelectionRepository

class AddQuoteToTagAdapter(
    private val quoteId: ID,
    private val tagId: ID,
    private val permissionManager: UserPermissionManager,
    repositoryProvider: RepositoryProvider,
) : UseCaseAdapter<Pair<Quote, Tag>> {

    private val quoteRepository = repositoryProvider.getRepository<QuoteRepository>()
    private val tagRepository = repositoryProvider.getRepository<TagRepository>()
    private val selectionRepository = repositoryProvider.getRepository<TagSelectionRepository>()

    override fun hasPermissions(user: User?): Boolean {
        return permissionManager.hasModeratorPermission(user)
    }

    override fun getItem(): Pair<Quote, Tag>? {
        val quote = quoteRepository.findById(quoteId) ?: return null
        val tag = tagRepository.findById(tagId) ?: return null
        return quote to tag
    }

    override fun addItem(item: Pair<Quote, Tag>) {
        selectionRepository.add(item.first, item.second)
    }
}
