package com.fndt.quote.rest.controllers

import com.fndt.quote.rest.dto.UserPrincipal
import com.fndt.quote.rest.factory.UsersUseCaseFactory
import io.ktor.auth.*

class AuthController(private val useCaseManager: UsersUseCaseFactory) {
    fun addBasicAuth(authentication: Authentication.Configuration) = authentication.apply {
        basic {
            realm = "Ktor"
            validate { credentials ->
                try {
                    useCaseManager.authUseCase(credentials.name, credentials.password).run().let { UserPrincipal(it) }
                } catch (e: IllegalStateException) {
                    null
                }
            }
        }
    }
}
