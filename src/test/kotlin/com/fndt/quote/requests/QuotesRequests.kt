package com.fndt.quote.requests

import com.fndt.quote.*
import com.fndt.quote.rest.dto.AddQuote
import com.fndt.quote.rest.dto.LikeRequest
import com.fndt.quote.rest.dto.QuoteReview
import com.fndt.quote.rest.dto.out.OutQuote
import com.fndt.quote.rest.dto.out.OutQuoteList
import com.fndt.quote.rest.util.DAY_ENDPOINT
import com.fndt.quote.rest.util.LIKE_ENDPOINT
import com.fndt.quote.rest.util.QUOTES_ENDPOINT
import com.fndt.quote.rest.util.REVIEW_QUOTE_ENDPOINT
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*

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
fun TestApplicationEngine.getQuoteOfTheDay(
    credentials: String
): OutQuote {
    val call = handleRequestWithAuth(HttpMethod.Get, "$QUOTES_ENDPOINT$DAY_ENDPOINT", credentials)
    return serializer.parseResponse(call.response)
}
