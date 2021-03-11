package com.fndt.quote.controllers

import com.fndt.quote.domain.QuotesBrowseService
import com.fndt.quote.domain.QuotesEditService
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.SerializationException

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
                    call.receiveCatching<Quote> { quote ->
                        editService.upsertQuote(quote)
                    }
                }
                post("/like") {
                    call.receiveCatching<Like> { like ->
                        browseService.setQuoteLike(like)
                        respondText("Like put successfully")
                    }
                }
            }
        }
    }
}

suspend inline fun <reified T : Any> ApplicationCall.receiveCatching(block: ApplicationCall.(T) -> Unit) {
    try {
        block(receive())
    } catch (e: SerializationException) {
        respondText(text = "Malformed json", status = HttpStatusCode.UnsupportedMediaType)
    }
}
