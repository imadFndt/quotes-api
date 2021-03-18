package com.fndt.quote.integrations

import com.fndt.quote.data.*
import com.fndt.quote.data.util.populateDb
import com.fndt.quote.data.util.quotesList
import com.fndt.quote.data.util.usersList
import com.fndt.quote.domain.dao.*
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.services.RegularUserService
import com.fndt.quote.domain.services.implementations.RegularUserServiceImpl
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RegularUserServiceImplAndDao {
    var commentDao: CommentDao = CommentDaoImpl(DatabaseProvider)

    var quoteDao: QuoteDao = QuoteDaoImpl(DatabaseProvider)

    var likeDao: LikeDao = LikeDaoImpl(DatabaseProvider)

    var tagDao: TagDao = TagDaoImpl(DatabaseProvider)

    var userDao: UserDao = UserDaoImpl(DatabaseProvider)

    private val quotes = quotesList.toMutableList()

    private val users = usersList

    private var service: RegularUserService = RegularUserServiceImpl(commentDao, quoteDao, likeDao, tagDao, userDao)

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
    fun `put like`() = runBlocking {
        val like = Like(1, 2)
        val success = service.setQuoteLike(like, true)

        assertTrue(success)
    }

    @Test
    fun `remove like`() = runBlocking {
        val like = Like(1, 1)

        service.setQuoteLike(like, false)
        assertNull(likeDao.find(like))
    }

    @Test
    fun `like when exists`() {
        val like = Like(1, 1)
        assertThrows<IllegalArgumentException> {
            runBlocking {
                service.setQuoteLike(like, true)
            }
        }
    }

    @Test
    fun `remove like when not exists`() {
        val like = Like(1, 2)
        assertThrows<IllegalArgumentException> {
            runBlocking {
                service.setQuoteLike(like, false)
            }
        }
    }

    @Test
    fun `remove quote`() = runBlocking {
        val success = service.removeQuote(1)

        assertTrue(success)
    }

    @Test
    fun `get comments`() = runBlocking {
        val quote = quotes[0]

        val list = service.getComments(quote.id)

        assertNotNull(list)
        Unit
    }

    @Test
    fun `add comment`() = runBlocking {
        val quote = quotes[0]
        val user = users[0]
        val dummyComment = Comment(1, "Dummy body", quote.id, 0, user.id)

        val comment = service.addComment(dummyComment.body, quote.id, user.id)

        assertNotNull(comment)
        Unit
    }

    @Test
    fun `add comment fail`() {
        val user = users[0]
        val dummyComment = Comment(1, "Dummy body", 312312, 0, user.id)

        assertThrows<IllegalArgumentException> {
            runBlocking {
                service.addComment(dummyComment.body, dummyComment.quoteId, dummyComment.user)
            }
        }
    }

    @Test
    fun `add quote`() = runBlocking {
        val quote = service.addQuote("ada", 1)

        assertTrue(service.getQuotes().contains(quote))
    }

    @Test
    fun `update quote`() = runBlocking {
        val quote = service.updateQuote(1, "bebe")
        assertTrue(quote.body == "bebe" && quote.id == 1)
    }
}
