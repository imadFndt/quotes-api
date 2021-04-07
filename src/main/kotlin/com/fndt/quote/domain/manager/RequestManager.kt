package com.fndt.quote.domain.manager

interface RequestManager {
    suspend fun <T> execute(block: suspend () -> T): T
}
