package com.fndt.quote.domain.services.implementations

import com.fndt.quote.data.util.quotesList
import com.fndt.quote.data.util.usersList
import com.fndt.quote.domain.dao.*
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.services.RegularUserService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class RegularUserServiceImplTest {

    @MockK(relaxed = true)
    lateinit var commentDao: CommentDao

    @MockK(relaxed = true)
    lateinit var quoteDao: QuoteDao

    @MockK(relaxed = true)
    lateinit var likeDao: LikeDao

    @MockK(relaxed = true)
    lateinit var tagDao: TagDao

    @MockK(relaxed = true)
    lateinit var userDao: UserDao

    private val quotes = quotesList.toMutableList()

    private val users = usersList

    private lateinit var service: RegularUserService

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        service = RegularUserServiceImpl(commentDao, quoteDao, likeDao, tagDao, userDao)
        coEvery { service.getQuotes() } returns quotesList
    }

    @Test
    fun `get quotes`() = runBlocking {
        val list = service.getQuotes()

        assertEquals(quotes, list)
    }

    @Test
    fun `put like`() = runBlocking {
        val like = Like(1, 1)
        every { likeDao.find(like) } returns null
        val likeSlot = slot<Like>()
        every { likeDao.like(capture(likeSlot)) } returns Like(1, 1)

        val success = service.setQuoteLike(like, true)

        assertTrue(success)
        verify(exactly = 1) {
            likeDao.find(like)
            likeDao.like(like)
        }
        verify(exactly = 0) { likeDao.unlike(like) }
    }

    @Test
    fun `remove like`() = runBlocking {
        val like = Like(1, 1)
        every { likeDao.find(like) } returns like
        val likeSlot = slot<Like>()
        every { likeDao.unlike(capture(likeSlot)) } returns 2

        val success = service.setQuoteLike(like, false)

        assertTrue(success)
        verify(exactly = 1) {
            likeDao.find(like)
            likeDao.unlike(like)
        }
        verify(exactly = 0) { likeDao.like(like) }
    }

    @Test
    fun `like when exists`() = runBlocking {
        val like = Like(1, 1)
        every { likeDao.find(like) } returns like

        val success = service.setQuoteLike(like, true)

        assertFalse(success)
        verify(exactly = 0) {
            likeDao.unlike(like)
            likeDao.like(like)
        }
    }

    @Test
    fun `remove like when not exists`() = runBlocking {
        val like = Like(1, 1)
        every { likeDao.find(like) } returns null

        val success = service.setQuoteLike(like, false)

        assertFalse(success)
        verify(exactly = 0) {
            likeDao.unlike(like)
            likeDao.like(like)
        }
    }

    @Test
    fun `remove quote`() = runBlocking {
        val quote = quotes[0]
        every { quoteDao.removeQuote(quote.id) } returns 2

        val success = service.removeQuote(1)

        assertTrue(success)
        verify(exactly = 1) { quoteDao.removeQuote(quote.id) }
    }

    @Test
    fun `get comments`() = runBlocking {
        val quote = quotes[0]
        every { commentDao.getComments(quote.id) } returns emptyList()

        val list = service.getComments(quote.id)

        assertNotNull(list)
        verify(exactly = 1) { commentDao.getComments(quote.id) }
    }

    @Test
    fun `add comment`() = runBlocking {
        val quote = quotes[0]
        val user = users[0]
        val dummyComment = Comment(1, "Dummy body", quote.id, 0, user.id)
        every { commentDao.insert(dummyComment.body, dummyComment.quoteId, dummyComment.user) } returns dummyComment
        coEvery { quoteDao.findById(any()) } returns quote

        service.addComment(dummyComment.body, quote.id, user.id)

        verify { commentDao.insert(dummyComment.body, dummyComment.quoteId, dummyComment.user) }
    }

    @Test
    fun `add comment fail`() = runBlocking {
        val quote = quotes[0]
        val user = users[0]
        val dummyComment = Comment(1, "Dummy body", quote.id, 0, user.id)
        every { commentDao.insert(dummyComment.body, dummyComment.quoteId, dummyComment.user) } returns dummyComment
        coEvery { quoteDao.findById(any()) } returns null

        assertThrows<IllegalArgumentException> {
            runBlocking {
                service.addComment(dummyComment.body, quote.id, user.id)
            }
        }

        verify(exactly = 0) { commentDao.insert(dummyComment.body, dummyComment.quoteId, dummyComment.user) }
    }

    @Test
    fun `add quote`() = runBlocking {
        coEvery { quoteDao.insert(any(), any()) } answers {
            Quote(10, "ada", System.currentTimeMillis(), User(1, "a")).apply {
                quotes.add(this)
            }
        }

        val quote = service.addQuote("ada", 1)

        assertTrue(quotes.contains(quote))
    }

    @Test
    fun `update quote`() = runBlocking {
        val idSlot = slot<Int>()
        val bodySlot = slot<String>()
        coEvery { quoteDao.update(quoteId = capture(idSlot), body = capture(bodySlot)) } answers {
            Quote(idSlot.captured, body = bodySlot.captured, quotes[0].createdAt, quotes[0].user, quotes[0].likes)
        }

        val quote = service.updateQuote(1, "bebe")
        assertTrue(quote.body == "bebe")
    }
}
