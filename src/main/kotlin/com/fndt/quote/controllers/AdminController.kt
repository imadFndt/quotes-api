package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.PermanentBan
import com.fndt.quote.controllers.dto.QuoteReview
import com.fndt.quote.controllers.dto.UpdateRole
import com.fndt.quote.controllers.factory.AdminUseCaseFactory
import com.fndt.quote.controllers.util.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

class AdminController(private val useCaseFactory: AdminUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth("") {
        reviewTag()
        changeRole()
        permanentBan()
    }

    private fun Route.reviewTag() {
        postExt(REVIEW_TAG_ENDPOINT) { principal ->
            val (decision, id) = receiveCatching<QuoteReview>() ?: run {
                respondText(text = BAD_JSON, status = HttpStatusCode.UnsupportedMediaType)
                return@postExt
            }
            useCaseFactory.getApproveTagUseCase(id, decision, principal.user)
            respondText(SUCCESS)
        }
    }

    private fun Route.changeRole() {
        postExt(ROLE_ENDPOINT) { principal ->
            val (role, id) = receiveCatching<UpdateRole>() ?: run {
                respondText(text = BAD_JSON, status = HttpStatusCode.UnsupportedMediaType)
                return@postExt
            }
            useCaseFactory.getChangeRoleUseCase(id, role, principal.user).run()
            respond(SUCCESS)
        }
    }

    private fun Route.permanentBan() {
        postExt(PERMANENT_BAN_ENDPOINT) { principal ->
            val (userId) = receiveCatching<PermanentBan>() ?: run {
                respondText(text = BAD_JSON, status = HttpStatusCode.UnsupportedMediaType)
                return@postExt
            }
            useCaseFactory.getPermanentBanUseCase(userId, principal.user).run()
            respond(SUCCESS)
        }
    }
}
