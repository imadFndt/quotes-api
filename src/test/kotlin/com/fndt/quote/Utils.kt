package com.fndt.quote

import com.fndt.quote.domain.filter.Access
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.usecases.get.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.net.URLEncoder

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

val serializer = Json {
    prettyPrint = true
    encodeDefaults = true
}

fun TestApplicationRequest.addJsonHeader() {
    addHeader("Content-Type", "application/json")
}

inline fun <reified T> Json.toJsonString(t: T): String {
    return encodeToJsonElement(t).toString()
}

inline fun <reified T> Json.parseResponse(response: TestApplicationResponse): T {
    val userString = response.content?.let { parseToJsonElement(it) } ?: throw Throwable()
    return decodeFromJsonElement(userString)
}

fun String.encode(): String {
    return URLEncoder.encode(this, "UTF-8")
}

fun createSearchMap(
    query: String? = null,
    authorId: Int? = null,
    userId: Int? = null,
    tagId: Int? = null,
    page: Int? = null,
    perPage: Int? = null,
    order: QuotesOrder? = null,
    access: Access? = null
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
