package com.fndt.quote.controllers

import com.fndt.quote.domain.dto.User
import io.ktor.auth.*

data class UserRolePrincipal(
    val user: User
) : Principal
