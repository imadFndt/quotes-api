package com.fndt.quote.integrations

import com.fndt.quote.data.*
import com.fndt.quote.data.util.populateDb
import com.fndt.quote.data.util.quotesList
import com.fndt.quote.data.util.usersList
import com.fndt.quote.domain.dao.*
import com.fndt.quote.domain.services.ModeratorUserService
import com.fndt.quote.domain.services.implementations.ModeratorUserServiceImpl
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class ModeratorUserServiceImplAndDao {
    var commentDao: CommentDao = CommentDaoImpl(DatabaseProvider)

    var quoteDao: QuoteDao = QuoteDaoImpl(DatabaseProvider)

    var likeDao: LikeDao = LikeDaoImpl(DatabaseProvider)

    var tagDao: TagDao = TagDaoImpl(DatabaseProvider)

    var userDao: UserDao = UserDaoImpl(DatabaseProvider)

    private val quotes = quotesList.toMutableList()

    private val users = usersList

    private var service: ModeratorUserService = ModeratorUserServiceImpl(userDao, commentDao, quoteDao, likeDao, tagDao)

    @BeforeEach
    fun initDB() {
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
    fun `ban user`() = runBlocking {
        val user = users[0]
        service.setBanState(user.id, 10000)

        assertTrue(service.getUserById(user.id).blockedUntil != null)
    }

    @Test
    fun `ban not existing user`() {
        assertThrows<IllegalArgumentException> {
            runBlocking {
                service.setBanState(2132131, 10000)
            }
        }
    }

    @Test
    fun `quote visibility`() = runBlocking {
        val quote = quotes[0]

        service.setQuoteVisibility(quote.id, true)
        assertTrue(service.getQuotes().find { it.id == quote.id }?.isPublic == true)

        service.setQuoteVisibility(quote.id, false)
        assertTrue(service.getQuotes().find { it.id == quote.id }?.isPublic == false)
    }

    @Test
    fun `not exist quote visibility`() {
        assertThrows<IllegalArgumentException> {
            runBlocking {
                service.setQuoteVisibility(1321321, true)
            }
        }
    }
}
