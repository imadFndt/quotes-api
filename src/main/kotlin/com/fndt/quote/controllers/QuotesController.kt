package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.AddComment
import com.fndt.quote.controllers.dto.AddQuote
import com.fndt.quote.controllers.util.receiveCatching
import com.fndt.quote.domain.QuotesEditService
import com.fndt.quote.domain.dto.Like
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

const val QUOTES_ENDPOINT = "/quotes"

class QuotesController(
    private val browseService: QuotesBrowseService,
    private val editService: QuotesEditService,
) : RoutingController {

    override fun route(routing: Routing) = routing {
        // todo refactor nesting
        route(QUOTES_ENDPOINT) {
            authenticate {
                get {
                    call.respond(browseService.getQuotes())
                }
                post {
                    call.receiveCatching<AddQuote> { quote ->
                        editService.upsertQuote(quote.body, quote.authorId, quote.tagsId, quote.quoteId)
                    }
                }
                post("/like") {
                    call.receiveCatching<Like> { like ->
                        when (principal<UserIdPrincipal>()?.name?.let { browseService.setQuoteLike(like, it) }) {
                            true -> respondText("Like success")
                            else -> respondText("Like failed", status = HttpStatusCode.Conflict)
                        }
                    }
                }
                get("{id}/comment") {
                    call.getAndCheckIntParameter("id")?.let {
                        call.respond(browseService.getComments(it))
                    }
                }
                post("{id}/comment") {
                    val userName = call.principal<UserIdPrincipal>()?.name
                    userName ?: return@post
                    call.getAndCheckIntParameter("id")?.let { quoteId ->
                        call.receiveCatching<AddComment> { comment ->
                            respond(browseService.addComment(comment.commentBody, quoteId, userName))
                        }
                    }
                }
                delete("/comment/{comment}") {
                    val userName = call.principal<UserIdPrincipal>()?.name
                    userName ?: return@delete

                    call.getAndCheckIntParameter("comment")?.let {
                        call.respond(browseService.deleteComment(it, userName))
                    }
                }
            }
        }
    }

    private suspend fun ApplicationCall.getAndCheckIntParameter(parameterName: String): Int? {
        return try {
            parameters[parameterName]?.toInt() ?: run {
                respondText("Missing or malformed id", status = HttpStatusCode.BadRequest)
                null
            }
        } catch (e: NumberFormatException) {
            respondText("Malformed id", status = HttpStatusCode.BadRequest)
            null
        }
    }
}
