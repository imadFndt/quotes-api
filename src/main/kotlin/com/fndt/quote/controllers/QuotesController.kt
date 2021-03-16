package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.AddComment
import com.fndt.quote.controllers.dto.AddQuote
import com.fndt.quote.controllers.dto.LikeRequest
import com.fndt.quote.controllers.util.*
import com.fndt.quote.domain.ServiceHolder
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.services.RegularUserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

const val QUOTES_ENDPOINT = "/quotes"

class QuotesController(private val holder: ServiceHolder) : RoutingController {

    override fun route(routing: Routing) = routing.routePathWithAuth(QUOTES_ENDPOINT) {
        getExt<RegularUserService>(holder = holder) { service -> respond(service.getQuotes()) }
        postExt<RegularUserService>(holder = holder) { service ->
            val quote = receiveCatching<AddQuote>()
            quote ?: return@postExt
            val result = service.upsertQuote(quote.body, quote.authorId, quote.tagsId, quote.quoteId)
            respondText(result.toString())
        }
        postExt<RegularUserService>("/like", holder) { service ->
            val (quoteId, action) = receiveCatching<LikeRequest>() ?: return@postExt
            val userId = principal<UserRolePrincipal>()?.user?.id
            val likeSuccess = service.setQuoteLike(Like(quoteId, userId!!), action)
            if (likeSuccess) {
                respondText("Like success")
            } else {
                respondText("Like failed", status = HttpStatusCode.Conflict)
            }
        }
        getExt<RegularUserService>("{id}/comment", holder) { service ->
            getAndCheckIntParameter("id")?.let { respond(service.getComments(it)) }
        }
        postExt<RegularUserService>("{id}/comment", holder) { service ->
            val principal = principal<UserRolePrincipal>() ?: return@postExt
            getAndCheckIntParameter("id")?.let { quoteId ->
                val comment = receiveCatching<AddComment>() ?: return@postExt
                respond(service.addComment(comment.commentBody, quoteId, principal.user.id))
            }
        }
        deleteExt<RegularUserService>("/comment/{comment}", holder) { service ->
            val principal = principal<UserRolePrincipal>() ?: return@deleteExt
            getAndCheckIntParameter("comment")?.let { respond(service.deleteComment(it, principal.user.id)) }
        }
    }
}
