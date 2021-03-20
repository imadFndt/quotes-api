package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.UserCredentials
import com.fndt.quote.controllers.util.receiveCatching
import com.fndt.quote.domain.manager.UsersUseCaseManager
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

const val REGISTRATION_ENDPOINT = "/register"

class RegistrationController(private val useCaseManager: UsersUseCaseManager) : RoutingController {
    override fun route(routing: Routing) = routing {
        suspend fun ApplicationCall.registerAndRespond() {
            val credentials = receiveCatching<UserCredentials>() ?: return
            val result = try {
                useCaseManager.registerUseCase(credentials.login, credentials.password).run()
                "Succeed" to HttpStatusCode.OK
            } catch (e: Exception) {
                "Failed" to HttpStatusCode.NotAcceptable
            }
            respondText(text = result.first, status = result.second)
        }
        route(REGISTRATION_ENDPOINT) { get { call.registerAndRespond() } }
    }
}
