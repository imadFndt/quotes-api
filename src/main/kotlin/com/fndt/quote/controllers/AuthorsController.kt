package com.fndt.quote.controllers

import com.fndt.quote.domain.QuotesBrowseService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

const val AUTHORS_ENDPOINT = "/authors"

class AuthorsController(private val browseService: QuotesBrowseService) : RoutingController {
    override fun route(routing: Routing) = routing {
        route(AUTHORS_ENDPOINT) {
            get("{id}") {
                val id = call.parameters["id"] ?: return@get call.respondText(
                    "Missing or malformed id", status = HttpStatusCode.BadRequest
                )
                try {
                    id.toInt()
                    call.respond(browseService.getQuotesByAuthorId(id.toInt()))
                } catch (e: NumberFormatException) {
                    call.respondText("Malformed id", status = HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
