package com.fndt.quote.di

import com.fndt.quote.controllers.*
import com.fndt.quote.controllers.factory.*
import com.fndt.quote.data.*
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.filter.QuotesFilter
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.manager.ProfilePictureManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.manager.implementations.PermissionManagerImpl
import com.fndt.quote.domain.manager.implementations.UserPermissionManagerImpl
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
        single<AuthorRepository> { AuthorRepositoryImpl(get()) }
    }
    val managerModule = module {
        factory<PermissionManager> { PermissionManagerImpl() }
        factory<UserPermissionManager> { UserPermissionManagerImpl() }
        factory<RequestManager> { RequestManagerImpl() }
        factory<QuotesFilter.Factory> { QuotesFilterImpl.FilterFactory(get()) }
        factory<ProfilePictureManager> { ProfilePictureManagerImpl() }
    }

    val useCaseManagerModule = module {
        factory { QuotesUseCaseFactory(get(), get(), get(), get(), get(), get()) }
        factory { CommentsUseCaseFactory(get(), get(), get(), get()) }
        factory { SelectionUseCaseFactory(get(), get(), get(), get(), get()) }
        factory { UsersUseCaseFactory(get(), get(), get(), get()) }
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
