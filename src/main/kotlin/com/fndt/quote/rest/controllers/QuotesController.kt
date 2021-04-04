package com.fndt.quote.rest.controllers

import com.fndt.quote.rest.dto.AddQuote
import com.fndt.quote.rest.dto.LikeRequest
import com.fndt.quote.rest.factory.QuotesUseCaseFactory
import com.fndt.quote.rest.util.*
import com.fndt.quote.domain.dto.Like
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

class QuotesController(private val useCaseFactory: QuotesUseCaseFactory) : RoutingController {

    override fun route(routing: Routing) = routing.routePathWithAuth(QUOTES_ENDPOINT) {
        addQuote()
        likeQuote()
    }

    private fun Route.addQuote() {
        postExt { principal ->
            val (quote, authorName) = receiveCatching<AddQuote>() ?: run {
                respondText(WRONG_PARAMETERS)
                return@postExt
            }
            useCaseFactory.addQuotesUseCase(quote, authorName, principal.user).run()
            respondText(SUCCESS)
        }
    }

    private fun Route.likeQuote() {
        postExt(LIKE_ENDPOINT) { principal ->
            val (quoteId, action) = receiveCatching<LikeRequest>() ?: return@postExt
            useCaseFactory.likeQuoteUseCase(Like(quoteId, principal.user.id), action, principal.user).run()
            respondText(LIKE_SUCCESS)
        }
    }
}
