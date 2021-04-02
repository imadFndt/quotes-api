package com.fndt.quote.domain.usecases

interface UseCase<T> {
    suspend fun run(): T
}
