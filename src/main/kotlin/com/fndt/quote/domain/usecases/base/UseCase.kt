package com.fndt.quote.domain.usecases.base

interface UseCase<T> {
    suspend fun run(): T
}
