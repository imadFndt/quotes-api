package com.fndt.quote.controllers

import com.fndt.quote.domain.dto.User
import io.ktor.auth.*

data class UserPrincipal(
    val user: User
) : Principal
