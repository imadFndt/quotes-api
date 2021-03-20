package com.fndt.quote.domain

interface RequestManager {
    suspend fun <T> execute(block: suspend () -> T): T
}
