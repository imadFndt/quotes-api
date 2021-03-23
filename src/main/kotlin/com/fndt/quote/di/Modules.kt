package com.fndt.quote.di

import com.fndt.quote.controllers.*
import com.fndt.quote.controllers.factory.*
import com.fndt.quote.data.*
import com.fndt.quote.domain.QuoteFilter
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.manager.implementations.PermissionManagerImpl
import com.fndt.quote.domain.repository.*
import org.koin.dsl.module

object Modules {
    val dbModule = module {
        DatabaseProvider.initDb
        single { DatabaseProvider }
        single<CommentRepository> { CommentRepositoryImpl(get()) }
        single<LikeRepository> { LikeRepositoryImpl(get()) }
        single<QuoteRepository> { QuoteRepositoryImpl(get()) }
        single<TagRepository> { TagRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }
    val managerModule = module {
        factory<PermissionManager> { PermissionManagerImpl() }
        factory<RequestManager> { RequestManagerImpl() }
        factory { UsersUseCaseFactory(get(), get(), get()) }
        factory<QuoteFilter.Builder.Factory> { QuoteFilterImpl.factory(get()) }
    }
    val useCaseManagerModule = module {
        factory { QuotesUseCaseFactory(get(), get(), get(), get(), get(), get()) }
        factory { CommentsUseCaseFactory(get(), get(), get(), get()) }
        factory { SelectionUseCaseFactory(get(), get(), get(), get()) }
        factory { UsersUseCaseFactory(get(), get(), get()) }
        factory { ModeratorUseCaseFactory(get(), get(), get(), get(), get()) }
        factory { AdminUseCaseFactory(get(), get(), get(), get()) }
    }

    val controllersModule = module {
        single { AuthController(get()) }
        single { QuotesController(get()) }
        single { CommentsController(get()) }
        single { UserController(get()) }
        single { SelectionsController(get()) }
        single { ModeratorController(get()) }
        single { AdminController(get()) }
    }
}
