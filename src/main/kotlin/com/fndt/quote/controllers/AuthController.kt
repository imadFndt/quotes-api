package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.UserPrincipal
import com.fndt.quote.controllers.factory.UsersUseCaseFactory
import io.ktor.auth.*

class AuthController(private val useCaseManager: UsersUseCaseFactory) {
    fun addBasicAuth(authentication: Authentication.Configuration) = authentication.apply {
        basic {
            realm = "Ktor"
            validate { credentials ->
                try {
                    useCaseManager.authUseCase(credentials.name, credentials.password).run().let {
                        UserPrincipal(it)
                    }
                } catch (e: IllegalStateException) {
                    null
                }
            }
        }
    }
}
