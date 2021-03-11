package com.fndt.quote.controllers

import com.fndt.quote.domain.AuthService
import com.fndt.quote.domain.dto.AuthRole
import io.ktor.auth.*

class AuthController(private val service: AuthService) {
    fun addBasicAuth(authentication: Authentication.Configuration) = authentication.apply {
        basic {
            realm = "Ktor"
            // TODO My principal
            validate { credentials ->
                if (service.checkCredentials(credentials) != AuthRole.NOT_AUTHORIZED) UserIdPrincipal(credentials.name) else null
            }
        }
    }
}
