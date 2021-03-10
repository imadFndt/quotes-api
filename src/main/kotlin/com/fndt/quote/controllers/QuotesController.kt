package com.fndt.quote.controllers

import com.fndt.quote.domain.QuotesBrowseService
import com.fndt.quote.domain.QuotesEditService
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val QUOTES_ENDPOINT = "/quotes"

class QuotesController(
    private val browseService: QuotesBrowseService,
    private val editService: QuotesEditService,
) : RoutingController {

    override fun route(routing: Routing) = routing {
        route(QUOTES_ENDPOINT) {
            authenticate {
                get {
                    call.respond(browseService.getQuotes())
                }
                post {
                    call.receiveOrNull<Quote>()?.let { editService.upsertQuote(it) }
                }
                post("/like") {
                    call.receiveOrNull<Like>()?.let {
                        browseService.setQuoteLike(it)
                        call.respondText("Like put successfully")
                    }
                }
            }
        }
    }
}
