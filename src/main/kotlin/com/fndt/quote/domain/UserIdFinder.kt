package com.fndt.quote.domain

interface UserIdFinder {
    suspend fun findIdByUserName(userName: String): Int
}
