package com.fndt.quote.rest.controllers

import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.filter.Access
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.usecases.get.*
import com.fndt.quote.rest.UrlSchemeProvider
import com.fndt.quote.rest.dto.AddQuote
import com.fndt.quote.rest.dto.LikeRequest
import com.fndt.quote.rest.dto.QuoteReview
import com.fndt.quote.rest.dto.out.toOutQuoteList
import com.fndt.quote.rest.factory.QuotesUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect

class QuotesController(private val useCaseFactory: QuotesUseCaseFactory) : RoutingController {

    override fun route(routing: Routing) = routing.routePathWithAuth(QUOTES_ENDPOINT) {
        getQuotes()
        addQuote()
        likeQuote()
        reviewQuote()
    }

    private fun Route.getQuotes() = getExt { principal ->
        processRequest {
            val args = initArgsMap()
            useCaseFactory.getQuoteSelectionsUseCase(args, principal.user).run()
        }.catch(defaultCatch()).collect { quotes ->
            respond(quotes.toOutQuoteList(UrlSchemeProvider))
        }
    }

    private fun Route.addQuote() = postExt { principal ->
        processRequest {
            val (quote, authorName) = receive<AddQuote>()
            useCaseFactory.addQuotesUseCase(quote, authorName, principal.user).run()
        }.respondPostDefault(this)
    }

    private fun Route.likeQuote() = postExt(LIKE_ENDPOINT) { principal ->
        processRequest {
            val (quoteId, action) = receive<LikeRequest>()
            useCaseFactory.likeQuoteUseCase(Like(quoteId, principal.user.id), action, principal.user).run()
        }.respondPostDefault(this)
    }

    private fun Route.reviewQuote() = postExt(REVIEW_ENDPOINT) { principal ->
        processRequest {
            val (decision, quoteId) = receive<QuoteReview>()
            useCaseFactory.getReviewQuoteUseCase(quoteId, decision, principal.user).run()
        }.respondPostDefault(this)
    }
}

private fun ApplicationCall.initArgsMap(): Map<String, Any?> {
    val args = mutableMapOf<String, Any?>()
    args[QUERY_KEY] = parameters[QUERY_KEY] as? Any
    args[AUTHOR_KEY] = parameters[AUTHOR_KEY]?.toInt() as? Any
    args[USER_KEY] = parameters[USER_KEY]?.toInt() as? Any
    args[TAG_KEY] = parameters[TAG_KEY]?.toInt() as? Any
    args[PAGE_KEY] = parameters[PAGE_KEY]?.toInt() as? Any
    args[PER_PAGE_KEY] = parameters[PER_PAGE_KEY]?.toInt() as? Any
    args[ORDER_KEY] = parameters.getOrder() as? Any
    args[ACCESS_KEY] = parameters.getAccess() as? Any
    return args
}

fun Parameters.getAccess(): Access? {
    return this[ACCESS_KEY]?.let { Access.findKey(it) ?: throw IllegalArgumentException(BAD_ACCESS) }
}

fun Parameters.getOrder(): QuotesOrder? {
    return this[ORDER_KEY]?.let { QuotesOrder.findKey(it) ?: throw IllegalArgumentException(BAD_ACCESS) }
}
