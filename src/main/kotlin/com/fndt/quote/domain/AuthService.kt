package com.fndt.quote.domain

import com.fndt.quote.domain.dto.AuthRole
import io.ktor.auth.*

interface AuthService {
    fun checkCredentials(credentials: UserPasswordCredential): AuthRole
}
