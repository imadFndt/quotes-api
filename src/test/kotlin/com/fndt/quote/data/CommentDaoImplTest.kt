package com.fndt.quote.data

import com.fndt.quote.data.util.populateDb
import com.fndt.quote.domain.dao.CommentDao
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class CommentDaoImplTest {
    private val commentDao: CommentDao = CommentDaoImpl(DatabaseProvider)

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
    fun `add comment`() {
        // WHEN
        val comment = commentDao.insert("Like it", 1, 1)
        comment ?: throw NullPointerException()
        val anotherComment = commentDao.insert("Dont like it", 1, 1)
        anotherComment ?: throw NullPointerException()
        val allComments = commentDao.getComments(1)

        // THEN
        assertTrue { allComments.containsAll(listOf(comment, anotherComment)) }
    }

    @Test
    fun `delete comment`() {
        // GIVEN
        val comment = commentDao.insert("Like it", 1, 1)
        comment ?: throw NullPointerException()
        val anotherComment = commentDao.insert("Dont like it", 1, 1)
        anotherComment ?: throw NullPointerException()

        // WHEN
        commentDao.remove(comment.id)
        val allComments = commentDao.getComments(1)
        // THEN
        assertTrue { !allComments.contains(comment) && allComments.contains(anotherComment) }
    }
}
