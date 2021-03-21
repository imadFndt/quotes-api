package com.fndt.quote.controllers

import com.fndt.quote.controllers.util.*
import com.fndt.quote.domain.manager.CommentsUseCaseManager
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

class CommentsController(private val commentsUseCaseManager: CommentsUseCaseManager) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth(COMMENTS_ENDPOINT) {
        getExt(COMMENTS_ENDPOINT) { principal ->
            getAndCheckIntParameter(ID)?.let {
                respond(commentsUseCaseManager.getCommentsUseCase(it, principal.user).run())
            }
        }
        postExt(COMMENTS_ENDPOINT) { principal ->
            val quoteId = getAndCheckIntParameter(ID) ?: run {
                respondText("Parameter fail", status = HttpStatusCode.BadRequest)
                return@postExt
            }
            val body = receiveText()
            val result = commentsUseCaseManager.addCommentsUseCase(body, quoteId, principal.user).run()
            respond(result)
        }
    }
}
