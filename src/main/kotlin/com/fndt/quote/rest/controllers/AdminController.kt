package com.fndt.quote.rest.controllers

import com.fndt.quote.rest.dto.PermanentBan
import com.fndt.quote.rest.dto.QuoteReview
import com.fndt.quote.rest.dto.UpdateRole
import com.fndt.quote.rest.factory.AdminUseCaseFactory
import com.fndt.quote.rest.util.*
import io.ktor.request.*
import io.ktor.routing.*

class AdminController(private val useCaseFactory: AdminUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth("") {
        reviewTag()
        changeRole()
        permanentBan()
    }

    private fun Route.reviewTag() {
        postExt(REVIEW_TAG_ENDPOINT) { principal ->
            processRequest {
                val (decision, id) = receive<QuoteReview>()
                useCaseFactory.getApproveTagUseCase(id, decision, principal.user).run()
            }.respondPostDefault(this)
        }
    }

    private fun Route.changeRole() {
        postExt(ROLE_ENDPOINT) { principal ->
            processRequest {
                val (role, id) = receive<UpdateRole>()
                useCaseFactory.getChangeRoleUseCase(id, role, principal.user).run()
            }.respondPostDefault(this)
        }
    }

    private fun Route.permanentBan() {
        postExt(PERMANENT_BAN_ENDPOINT) { principal ->
            processRequest {
                val (userId) = receive<PermanentBan>()
                useCaseFactory.getPermanentBanUseCase(userId, principal.user).run()
            }.respondPostDefault(this)
        }
    }
}
