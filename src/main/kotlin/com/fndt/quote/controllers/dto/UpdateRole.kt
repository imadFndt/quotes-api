package com.fndt.quote.controllers.dto

import com.fndt.quote.domain.dto.AuthRole
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRole(val role: AuthRole, val id: Int)
