package com.fndt.quote.rest.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(val login: String, val password: String)
