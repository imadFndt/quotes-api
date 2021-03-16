package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.AddComment
import com.fndt.quote.controllers.dto.AddQuote
import com.fndt.quote.controllers.dto.LikeRequest
import com.fndt.quote.controllers.util.receiveCatching
import com.fndt.quote.domain.UserServiceHolder
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.services.RegularUserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

const val QUOTES_ENDPOINT = "/quotes"

class QuotesController(private val serviceFactory: UserServiceHolder) : RoutingController {

    override fun route(routing: Routing) = routing {
        // todo refactor nesting
        route(QUOTES_ENDPOINT) {
            authenticate {
                get {
                    call.getServiceOrRespondFail<RegularUserService>()?.let { call.respond(it.getQuotes()) }
                }
                post {
                    val quote = call.receiveCatching<AddQuote>()
                    quote ?: return@post
                    call.getServiceOrRespondFail<RegularUserService>()
                        ?.upsertQuote(quote.body, quote.authorId, quote.tagsId, quote.quoteId)
                }
                post("/like") {
                    val (quoteId, action) = call.receiveCatching<LikeRequest>() ?: return@post
                    val userId = call.principal<UserRolePrincipal>()?.user?.id
                    val likeSuccess = call.getServiceOrRespondFail<RegularUserService>()
                        ?.setQuoteLike(Like(quoteId, userId!!), action)
                    if (likeSuccess == true) {
                        call.respondText("Like success")
                    } else {
                        call.respondText("Like failed", status = HttpStatusCode.Conflict)
                    }
                }
                get("{id}/comment") {
                    val service = call.getServiceOrRespondFail<RegularUserService>() ?: return@get
                    call.getAndCheckIntParameter("id")?.let { call.respond(service.getComments(it)) }
                }
                post("{id}/comment") {
                    val service = call.getServiceOrRespondFail<RegularUserService>() ?: return@post
                    val principal = call.principal<UserRolePrincipal>() ?: return@post
                    call.getAndCheckIntParameter("id")?.let { quoteId ->
                        val comment = call.receiveCatching<AddComment>() ?: return@post
                        call.respond(service.addComment(comment.commentBody, quoteId, principal.user.id))
                    }
                }
                delete("/comment/{comment}") {
                    val service = call.getServiceOrRespondFail<RegularUserService>() ?: return@delete
                    val principal = call.principal<UserRolePrincipal>() ?: return@delete
                    call.getAndCheckIntParameter("comment")?.let {
                        call.respond(service.deleteComment(it, principal.user.id))
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

    private suspend fun <T : RegularUserService> ApplicationCall.getServiceOrRespondFail(): T? {
        val principal = principal<UserRolePrincipal>()
        return serviceFactory.getService(principal?.user?.role) ?: run {
            respondText("Request failed", status = HttpStatusCode.BadRequest)
            null
        }
    }
}
