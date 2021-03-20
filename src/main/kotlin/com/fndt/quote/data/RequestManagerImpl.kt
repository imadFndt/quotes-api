package com.fndt.quote.data

import com.fndt.quote.domain.RequestManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class RequestManagerImpl : RequestManager {
    override suspend fun <T> execute(block: suspend () -> T) = newSuspendedTransaction {
        return@newSuspendedTransaction block()
    }
}
