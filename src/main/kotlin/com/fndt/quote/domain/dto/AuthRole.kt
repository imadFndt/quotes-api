package com.fndt.quote.domain.dto

enum class AuthRole(val byte: Byte) { NOT_AUTHORIZED(0b0000000), REGULAR(0b0000001), ADMIN(0b0000010) }
