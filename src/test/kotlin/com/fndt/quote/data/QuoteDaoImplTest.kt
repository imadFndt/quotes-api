package com.fndt.quote.data

import com.fndt.quote.data.util.authorList
import com.fndt.quote.data.util.quotesList
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
            transaction {
                val tables = arrayOf(
                    DatabaseProvider.Quotes,
                    DatabaseProvider.Authors,
                    DatabaseProvider.Users,
                    DatabaseProvider.Tags,
                    DatabaseProvider.TagsOnQuotes,
                    DatabaseProvider.Comments,
                    DatabaseProvider.LikesOnQuotes,
                )
                SchemaUtils.drop(*tables)
                SchemaUtils.create(*tables)
                DatabaseProvider.Authors.batchInsert(authorList) { author ->
                    this[DatabaseProvider.Authors.name] = author.name
                }
                DatabaseProvider.Quotes.batchInsert(quotesList) { quote ->
                    this[DatabaseProvider.Quotes.body] = quote.body
                    this[DatabaseProvider.Quotes.createdAt] = System.currentTimeMillis()
                    this[DatabaseProvider.Quotes.isPublic] = true
                    val authorId = DatabaseProvider.Authors
                        .slice(DatabaseProvider.Authors.id)
                        .select { DatabaseProvider.Authors.name eq quote.author.name }
                        .limit(1)
                        .firstOrNull()
                        ?.let { it[DatabaseProvider.Authors.id] } ?: run { throw IllegalArgumentException() }
                    this[DatabaseProvider.Quotes.author] = authorId
                }
            }
        }
    }

    @Test
    fun `Gets all quotes right`() {
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
    fun `Get all quotes by author id`() {
        // WHEN 
        val quotesById = quoteDao.getQuotes(1)

        // THEN
        assertFalse { quotesById.all { it.author.id != 1 } }
        assertTrue { quotesById.all { it.author.id == 1 } }
    }

    @Test
    fun `Get all quotes by access`() {
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
    fun `Get all quotes by author and access`() {
        // GIVEN
        val expectedList = quoteDao.getQuotes().filter { it.id == 1 && !it.isPublic }

        // WHEN
        val actualList = quoteDao.getQuotes(1, false)

        // THEN
        assert(expectedList.containsAll(actualList) && actualList.containsAll(expectedList))
    }

    @Test
    fun `Insert quote`() = transaction {
        // WHEN
        val quote = quoteDao.insert("Привет", 1)
        quote ?: throw NullPointerException()

        // THEN
        assertTrue {
            quote == quoteDao.findById(quote.id) && quote.body == "Привет" && quote.author.id == 1
        }
    }

    // TODO CHECK DOUBLE INSERT
    @Test
    fun `Updating quote`() {
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
    fun `Deleting quote`() {
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
}
