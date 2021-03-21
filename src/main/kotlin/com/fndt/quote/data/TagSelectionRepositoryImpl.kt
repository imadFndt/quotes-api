package com.fndt.quote.data

import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.repository.TagSelectionRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert

class TagSelectionRepositoryImpl(dbProvider: DatabaseProvider) : TagSelectionRepository {
    private val tagsQuotesTable: DatabaseProvider.TagsOnQuotes by dbProvider

    override fun add(quote: Quote, tag: Tag) {
        tagsQuotesTable.insert { insert ->
            insert[DatabaseProvider.TagsOnQuotes.tag] = tag.id
            insert[DatabaseProvider.TagsOnQuotes.quote] = quote.id
        }[DatabaseProvider.TagsOnQuotes.tag].value
    }

    override fun remove(quote: Quote, tag: Tag) {
        tagsQuotesTable.deleteWhere { (DatabaseProvider.TagsOnQuotes.quote eq quote.id) and (DatabaseProvider.TagsOnQuotes.tag eq tag.id) }
    }

    override fun getSelectionByTag(tag: Tag): List<Quote> {
        TODO("Not yet implemented")
    }
}
