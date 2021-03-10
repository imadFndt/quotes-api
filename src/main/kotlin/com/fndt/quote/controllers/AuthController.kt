package com.fndt.quote.controllers

import com.fndt.quote.domain.AuthService
import io.ktor.auth.*

class AuthController(private val service: AuthService) {
    fun addBasicAuth(authentication: Authentication.Configuration) = authentication.apply {
        basic {
            realm = "Ktor"
            validate { credentials ->
                if (credentials.name == credentials.password) UserIdPrincipal(credentials.name) else null
            }
        }
    }
}
