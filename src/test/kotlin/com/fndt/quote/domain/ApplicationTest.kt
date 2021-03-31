package com.fndt.quote.domain

import com.fndt.quote.controllers.dto.AddQuote
import com.fndt.quote.controllers.dto.LikeRequest
import com.fndt.quote.controllers.dto.UserCredentials
import com.fndt.quote.controllers.dto.out.OutQuoteList
import com.fndt.quote.controllers.util.LIKE_ENDPOINT
import com.fndt.quote.controllers.util.QUOTES_ENDPOINT
import com.fndt.quote.controllers.util.REGISTRATION_ENDPOINT
import com.fndt.quote.controllers.util.ROLE_ENDPOINT
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.usecases.*
import com.fndt.quote.module
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URLEncoder
import kotlin.test.assertEquals

@InternalAPI
class ApplicationTest {

    private val regularCredentials = "regular:a"
    private val moderatorCredentials = "moderator:a"

    private val serializer = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    @Test
    fun registration() = withTestApplication(Application::module) {
        with(
            handleRequest(HttpMethod.Get, REGISTRATION_ENDPOINT) {
                addJsonHeader()
                setBody(serializer.encodeToJsonElement(UserCredentials("a", "a")).toString())
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }

    @Test
    fun auth() = withTestApplication(Application::module) {
        with(handleRequestWithAuth(HttpMethod.Get, ROLE_ENDPOINT, regularCredentials)) {
            runBlocking {
                val user = serializer.parseResponse<User>(response)
                assertEquals(AuthRole.REGULAR, user.role)
            }
        }
    }

    @Test
    fun `quotes use cases`() = withTestApplication(Application::module) {
        val newBody = "New quote"
        val newAuthor = "Barash"
        val listBefore = getQuotes(createSearchMap(access = QuotesAccess.ALL))

        handleRequestWithAuth(HttpMethod.Post, QUOTES_ENDPOINT, regularCredentials) {
            addJsonHeader()
            setBody(serializer.toJsonString(AddQuote(newBody, newAuthor)))
        }.run {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        handleRequestWithAuth(HttpMethod.Post, "$QUOTES_ENDPOINT$LIKE_ENDPOINT", regularCredentials) {
            addJsonHeader()
            setBody(serializer.toJsonString(LikeRequest(6, likeAction = true)))
        }.run {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        val listAfter = getQuotes(createSearchMap(access = QuotesAccess.ALL))
        val result = listAfter.quotes subtract listBefore.quotes
        assertTrue(
            result.size == 1 && result.firstOrNull()
                ?.run { body == newBody && author.name == newAuthor && likes == 1 } ?: false
        )
    }

    private fun TestApplicationEngine.getQuotes(args: Map<String, Any?>): OutQuoteList {
        val urlArgs = args
            .filter { it.value != null }
            .map { "${it.key.encode()}=${it.value.toString().encode()}" }
            .reduce { acc, current -> "$acc$current" }

        val call = handleRequestWithAuth(HttpMethod.Get, "$QUOTES_ENDPOINT?$urlArgs", moderatorCredentials)
        return serializer.parseResponse(call.response)
    }

    private fun TestApplicationEngine.handleRequestWithAuth(
        get: HttpMethod,
        endpoint: String,
        credentials: String,
        function: (TestApplicationRequest.() -> Unit)? = null
    ): TestApplicationCall {
        return handleRequest(get, endpoint) {
            addHeader("Authorization", "Basic ${credentials.encodeBase64()}")
            function?.invoke(this)
        }
    }
}

private fun TestApplicationRequest.addJsonHeader() {
    addHeader("Content-Type", "application/json")
}

private inline fun <reified T> Json.toJsonString(t: T): String {
    return encodeToJsonElement(t).toString()
}

inline fun <reified T> Json.parseResponse(response: TestApplicationResponse): T {
    val userString = response.content?.let { parseToJsonElement(it) } ?: throw Throwable()
    return decodeFromJsonElement(userString)
}

private fun String.encode(): String {
    return URLEncoder.encode(this, "UTF-8")
}

private fun createSearchMap(
    query: String? = null,
    authorId: Int? = null,
    userId: Int? = null,
    tagId: Int? = null,
    page: Int? = null,
    perPage: Int? = null,
    order: QuotesOrder? = null,
    access: QuotesAccess? = null
) = mapOf(
    QUERY_KEY to query,
    AUTHOR_KEY to authorId,
    USER_KEY to userId,
    TAG_KEY to tagId,
    PAGE_KEY to page,
    PER_PAGE_KEY to perPage,
    ORDER_KEY to order?.key,
    ACCESS_KEY to access?.key
)
