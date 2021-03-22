package com.fndt.quote.domain

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User

abstract class QuoteFilter {
    abstract fun getQuotes(): List<Quote>

    fun findQuote(): Quote? {
        return getQuotes().firstOrNull()
    }

    abstract class Builder {
        protected var tag: Tag? = null
        protected var user: User? = null
        protected var isPublic: Boolean? = null
        protected var orderPopulars: Boolean = false
        protected var query: String? = null
        protected var quoteId: ID? = null

        fun setTag(tag: Tag?) = apply {
            this.tag = tag
        }

        fun setUser(user: User?) = apply {
            this.user = user
        }

        fun setAccess(isPublic: Boolean?) = apply {
            this.isPublic = isPublic
        }

        fun setOrderPopulars(orderPopulars: Boolean) = apply {
            this.orderPopulars = orderPopulars
        }

        fun setQuery(query: String?) = apply {
            this.query = query
        }

        fun setQuoteId(id: ID?) = apply {
            this.quoteId = id
        }

        abstract fun build(): QuoteFilter

        interface Factory {
            fun create(): Builder
        }
    }
}
