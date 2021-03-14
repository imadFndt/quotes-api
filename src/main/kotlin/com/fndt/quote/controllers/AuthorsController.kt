package com.fndt.quote.controllers

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

const val AUTHORS_ENDPOINT = "/authors"

class AuthorsController(private val browseService: QuotesBrowseService) : RoutingController {
    override fun route(routing: Routing) = routing {
        suspend fun ApplicationCall.respondQuotesById() {
            val id =
                parameters["id"] ?: return respondText("Missing or malformed id", status = HttpStatusCode.BadRequest)
            try {
                respond(browseService.getQuotes(id.toInt()))
            } catch (e: NumberFormatException) {
                respondText("Malformed id", status = HttpStatusCode.BadRequest)
            }
        }
        route(AUTHORS_ENDPOINT) {
            get("{id}") { call.respondQuotesById() }
        }
    }
}
