package com.fndt.quote.data

import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.repository.TagSelectionRepository
import org.jetbrains.exposed.sql.*

class TagSelectionRepositoryImpl(dbProvider: DatabaseProvider) : TagSelectionRepository {
    private val tagsQuotesTable: DatabaseProvider.TagsOnQuotes by dbProvider

    override fun get(): Map<Int, Int> {
        return tagsQuotesTable.selectAll()
            .map {
                it[tagsQuotesTable.quote].value to it[tagsQuotesTable.tag].value
            }.toMap()
    }

    override fun add(quote: Quote, tag: Tag) {
        if (pairExists(quote, tag)) return
        tagsQuotesTable.insert { insert ->
            insert[DatabaseProvider.TagsOnQuotes.tag] = tag.id
            insert[DatabaseProvider.TagsOnQuotes.quote] = quote.id
        }[DatabaseProvider.TagsOnQuotes.tag].value
    }

    override fun remove(quote: Quote, tag: Tag) {
        tagsQuotesTable.deleteWhere { (DatabaseProvider.TagsOnQuotes.quote eq quote.id) and (DatabaseProvider.TagsOnQuotes.tag eq tag.id) }
    }

    private fun pairExists(quote: Quote, tag: Tag): Boolean {
        return tagsQuotesTable.select {
            (tagsQuotesTable.tag eq tag.id) and (tagsQuotesTable.quote eq quote.id)
        }.firstOrNull()?.let { true } ?: false
    }
}
