package com.fndt.quote.controllers

import com.fndt.quote.domain.manager.UsersUseCaseManager
import io.ktor.auth.*

class AuthController(private val useCaseManager: UsersUseCaseManager) {
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
