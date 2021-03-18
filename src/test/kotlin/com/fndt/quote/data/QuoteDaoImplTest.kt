package com.fndt.quote.data

import com.fndt.quote.data.util.populateDb
import com.fndt.quote.data.util.quotesList
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class QuoteDaoImplTest {
    private val dbProvider = DatabaseProvider
    private val quoteDao = QuoteDaoImpl(dbProvider)

    @BeforeEach
    fun initDb() {
        Database.connect(
            "jdbc:h2:mem;DB_CLOSE_DELAY=-1",
            "org.h2.Driver",
            "root",
            ""
        ).apply {
            populateDb()
        }
    }

    @Test
    fun `gets all quotes right`() {
        // WHEN
        val quotesDbList = quoteDao.getQuotes()
        // THEN
        assertTrue {
            quotesDbList.all {
                it.id == (quotesList.find { quoteId -> quoteId.id == it.id })?.id ||
                    it.body == (quotesList.find { quoteId -> quoteId.body == it.body })?.body
            }
        }
    }

    @Test
    fun `get all quotes by access`() {
        // GIVEN
        val privateQuotes = quoteDao.getQuotes(isPublic = false)
        val trueQuotes = quoteDao.getQuotes(isPublic = true)
        val actualList = privateQuotes.toMutableList().apply { addAll(trueQuotes) }

        // WHEN
        val expectedList = quoteDao.getQuotes()

        // THEN
        assert(expectedList.containsAll(actualList) && actualList.containsAll(expectedList))
    }

    @Test
    fun `get all quotes by author and access`() {
        // GIVEN
        val expectedList = quoteDao.getQuotes().filter { it.id == 1 && !it.isPublic }

        // WHEN
        val actualList = quoteDao.getQuotes(1, false)

        // THEN
        assert(expectedList.containsAll(actualList) && actualList.containsAll(expectedList))
    }

    @Test
    fun `insert quote`() = transaction {
        // WHEN
        val quote = quoteDao.insert("Привет", 1)
        quote ?: throw NullPointerException()

        // THEN
        assertTrue {
            quote == quoteDao.findById(quote.id) && quote.body == "Привет" && quote.user.id == 1
        }
    }

    // TODO CHECK DOUBLE INSERT
    @Test
    fun `updating quote`() {
        // GIVEN
        val expectedQuote = quoteDao.findById(1)?.apply {
            body = "New body"
        }

        // WHEN
        val actualQuote = quoteDao.update(expectedQuote!!.id, body = expectedQuote.body)

        // THEN
        assertEquals(expectedQuote, actualQuote)
    }

    @Test
    fun `deleting quote`() {
        // GIVEN
        val expectedList = quoteDao.getQuotes().toMutableList()
        val removingQuote = quotesList[0]
        quoteDao.removeQuote(removingQuote.id)
        expectedList.removeAt(0)

        // WHEN
        val actualList = quoteDao.getQuotes()

        // THEN
        assertEquals(expectedList, actualList)
    }

    // TODO
    @Test
    fun `find by id `() {
        val quote = quoteDao.findById(1)
    }

    // TODO
    @Test
    fun `find by id`() {
        val quotes = quoteDao.findByUserId(1)
    }
}
