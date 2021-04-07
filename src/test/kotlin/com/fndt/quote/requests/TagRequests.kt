package com.fndt.quote.requests

import com.fndt.quote.domain.*
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.rest.dto.AddQuoteToTag
import com.fndt.quote.rest.dto.AddTag
import com.fndt.quote.rest.dto.TagReview
import com.fndt.quote.rest.util.ADD_ENDPOINT
import com.fndt.quote.rest.util.REVIEW_ENDPOINT
import com.fndt.quote.rest.util.TAG_ENDPOINT
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*

@InternalAPI
fun TestApplicationEngine.getTags(credentials: String): List<Tag> {
    val call = handleRequestWithAuth(HttpMethod.Get, TAG_ENDPOINT, credentials)
    return serializer.parseResponse(call.response)
}

@InternalAPI
fun TestApplicationEngine.addTag(tagName: String, credentials: String): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, TAG_ENDPOINT, credentials) {
        addJsonHeader()
        setBody(serializer.toJsonString(AddTag(tagName)))
    }
}

@InternalAPI
fun TestApplicationEngine.reviewTag(review: TagReview, credentials: String): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, "$TAG_ENDPOINT/$REVIEW_ENDPOINT", credentials) {
        addJsonHeader()
        setBody(serializer.toJsonString(review))
    }
}

@InternalAPI
fun TestApplicationEngine.addQuoteToTag(addQuoteToTag: AddQuoteToTag, credentials: String): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, "$TAG_ENDPOINT/$ADD_ENDPOINT", credentials) {
        addJsonHeader()
        setBody(serializer.toJsonString(addQuoteToTag))
    }
}
