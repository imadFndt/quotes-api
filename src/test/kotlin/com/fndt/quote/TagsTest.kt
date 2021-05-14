package com.fndt.quote

import com.fndt.quote.domain.filter.Access
import com.fndt.quote.requests.*
import com.fndt.quote.rest.dto.AddQuoteToTag
import com.fndt.quote.rest.dto.TagReview
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@InternalAPI
class TagsTest {
    private val moderatorCredentials = "moderator:a"
    private val adminCredentials = "admin:a"

    @Test
    fun `add and review tag`(): Unit = withTestApplication(Application::module) {
        val tagName = "testTag"

        addTag(tagName, moderatorCredentials).run {
            assertEquals(HttpStatusCode.OK, response.status())
        }
        val listAfterModerator = getTags(moderatorCredentials)
        val item = listAfterModerator.find { it.name == tagName && !it.isPublic }
        assertTrue(item != null)

        reviewTag(TagReview(true, item.id), adminCredentials)
        val listAfterApprove = getTags(moderatorCredentials)
        assertTrue(listAfterApprove.find { it.id == item.id && it.isPublic } != null)

        addQuoteToTag(AddQuoteToTag(item.id, item.id), moderatorCredentials)
        addQuoteToTag(AddQuoteToTag(item.id, item.id), moderatorCredentials)
        val quotesAfterAdd = getQuotes(createSearchMap(access = Access.ALL), moderatorCredentials)
        val quote = quotesAfterAdd.quotes.find { it.id == item.id }
        assertNotNull(quote)
        val tagExists = quote.tags.find { it.id == item.id }
        assertNotNull(tagExists)
    }

    @Test
    fun `double tag add`(): Unit = withTestApplication(Application::module) {
        val tagName = "testTagA"

        addTag(tagName, moderatorCredentials).run {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        addTag(tagName, moderatorCredentials).run {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }
    }
}
