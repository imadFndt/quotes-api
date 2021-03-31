package com.fndt.quote.domain

import com.fndt.quote.controllers.dto.AddComment
import com.fndt.quote.controllers.dto.AddQuote
import com.fndt.quote.controllers.dto.LikeRequest
import com.fndt.quote.controllers.dto.QuoteReview
import com.fndt.quote.controllers.dto.out.OutQuote
import com.fndt.quote.controllers.dto.out.OutQuoteList
import com.fndt.quote.controllers.util.*
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.User
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*

@InternalAPI
fun TestApplicationEngine.getUserByCredentials(credentials: String): User {
    val call = handleRequestWithAuth(HttpMethod.Get, ROLE_ENDPOINT, credentials)
    return serializer.parseResponse(call.response)
}

@InternalAPI
fun TestApplicationEngine.sendNewQuote(
    body: String,
    author: String,
    credentials: String,
): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, QUOTES_ENDPOINT, credentials) {
        addJsonHeader()
        setBody(serializer.toJsonString(AddQuote(body, author)))
    }
}

@InternalAPI
fun TestApplicationEngine.sendLike(
    likeRequest: LikeRequest,
    credentials: String,
): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, "$QUOTES_ENDPOINT$LIKE_ENDPOINT", credentials) {
        addJsonHeader()
        setBody(serializer.toJsonString(likeRequest))
    }
}

@InternalAPI
fun TestApplicationEngine.approveQuote(
    outQuote: OutQuote,
    credentials: String,
): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, REVIEW_QUOTE_ENDPOINT, credentials) {
        addJsonHeader()
        setBody(serializer.toJsonString(QuoteReview(true, outQuote.id)))
    }
}

@InternalAPI
fun TestApplicationEngine.getQuotes(
    args: Map<String, Any?>,
    credentials: String
): OutQuoteList {
    val urlArgs = args
        .filter { it.value != null }
        .map { "${it.key.encode()}=${it.value.toString().encode()}" }
        .reduce { acc, current -> "$acc$current" }

    val call = handleRequestWithAuth(HttpMethod.Get, "$QUOTES_ENDPOINT?$urlArgs", credentials)
    return serializer.parseResponse(call.response)
}

@InternalAPI
fun TestApplicationEngine.addComment(
    quoteId: Int,
    addComment: AddComment,
    credentials: String
): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, "/quotes/$quoteId/comment", credentials) {
        addJsonHeader()
        setBody(serializer.toJsonString(addComment))
    }
}

@InternalAPI
fun TestApplicationEngine.getComments(
    quoteId: Int,
    credentials: String
): List<Comment> {
    val call = handleRequestWithAuth(HttpMethod.Get, "/quotes/$quoteId/comment", credentials)
    return serializer.parseResponse(call.response)
}

@InternalAPI
fun TestApplicationEngine.banUser(
    userId: Int,
    credentials: String
): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, "$BAN_ENDPOINT/$userId", credentials)
}

@InternalAPI
fun TestApplicationEngine.handleRequestWithAuth(
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
