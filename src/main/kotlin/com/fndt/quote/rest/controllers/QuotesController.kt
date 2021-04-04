package com.fndt.quote.rest.controllers

import com.fndt.quote.domain.dto.Like
import com.fndt.quote.rest.dto.AddQuote
import com.fndt.quote.rest.dto.LikeRequest
import com.fndt.quote.rest.factory.QuotesUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.request.*
import io.ktor.routing.*

class QuotesController(private val useCaseFactory: QuotesUseCaseFactory) : RoutingController {

    override fun route(routing: Routing) = routing.routePathWithAuth(QUOTES_ENDPOINT) {
        addQuote()
        likeQuote()
    }

    private fun Route.addQuote() {
        postExt { principal ->
            processRequest {
                val (quote, authorName) = receive<AddQuote>()
                useCaseFactory.addQuotesUseCase(quote, authorName, principal.user).run()
            }.defaultPostChain(this)
        }
    }

    private fun Route.likeQuote() {
        postExt(LIKE_ENDPOINT) { principal ->
            processRequest {
                val (quoteId, action) = receive<LikeRequest>()
                useCaseFactory.likeQuoteUseCase(Like(quoteId, principal.user.id), action, principal.user).run()
            }.defaultPostChain(this)
        }
    }
}
