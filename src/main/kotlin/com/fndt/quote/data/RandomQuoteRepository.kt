package com.fndt.quote.data

import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.repository.base.BaseRepository
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDate

class RandomQuoteRepository(
    databaseProvider: DatabaseProvider
) : BaseRepository {
    private val randomQuoteTable: DatabaseProvider.RandomQuotes by databaseProvider

    fun getRandomQuote(user: User, date: LocalDate): ID? {
        (randomQuoteTable).select {
            randomQuoteTable.user eq user.id
        }.toList().also { list ->
            return if (list.isNotEmpty() && list[0][randomQuoteTable.day] == date.hashCode()) {
                list[0][randomQuoteTable.quote].value
            } else {
                null
            }
        }
    }

    fun addRandomQuote(user: User, date: LocalDate, quote: Quote) {
        if (hasUser(user)) updateUser(user, date, quote) else insertUser(user, date, quote)
    }

    private fun insertUser(user: User, date: LocalDate, quote: Quote) {
        randomQuoteTable.insert {
            it[randomQuoteTable.day] = date.hashCode()
            it[randomQuoteTable.quote] = quote.id
            it[randomQuoteTable.user] = user.id
        }
    }

    private fun updateUser(user: User, date: LocalDate, quote: Quote) {
        randomQuoteTable.update({ randomQuoteTable.user eq user.id }) {
            it[randomQuoteTable.day] = date.hashCode()
            it[randomQuoteTable.quote] = quote.id
        }
    }

    private fun hasUser(user: User): Boolean = randomQuoteTable.select {
        randomQuoteTable.user eq user.id
    }.toList().firstOrNull() != null
}
