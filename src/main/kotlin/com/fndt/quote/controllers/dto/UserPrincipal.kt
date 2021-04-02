package com.fndt.quote.controllers.dto

import com.fndt.quote.domain.dto.User
import io.ktor.auth.*

data class UserPrincipal(
    val user: User
) : Principal
