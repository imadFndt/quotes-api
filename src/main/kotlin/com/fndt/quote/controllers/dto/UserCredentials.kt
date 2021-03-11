package com.fndt.quote.controllers.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(val login: String, val password: String)
