package com.fndt.quote.di

import com.fndt.quote.controllers.*
import com.fndt.quote.controllers.factory.*
import com.fndt.quote.data.*
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.filter.QuotesFilter
import com.fndt.quote.domain.manager.ProfilePictureManager
import com.fndt.quote.domain.manager.UserPermissionManager
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
        single<TagSelectionRepository> { TagSelectionRepositoryImpl(get()) }
    }

    val managerModule = module {
        factory<UserPermissionManager> { UserPermissionManagerImpl() }
        factory<RequestManager> { RequestManagerImpl() }
        factory<QuotesFilter.Factory> { QuotesFilterImpl.FilterFactory(get()) }
    }

    fun imagesModule(path: String) = module {
        factory<ProfilePictureManager> { ProfilePictureManagerImpl(path) }
    }

    val useCaseManagerModule = module {
        factory { QuotesUseCaseFactory(get(), get(), get(), get(), get(), get()) }
        factory { CommentsUseCaseFactory(get(), get(), get(), get()) }
        factory { SelectionUseCaseFactory(get(), get(), get(), get(), get(), get()) }
        factory { UsersUseCaseFactory(get(), get(), get(), get()) }
        factory { ModeratorUseCaseFactory(get(), get(), get(), get(), get(), get()) }
        factory { AdminUseCaseFactory(get(), get(), get(), get()) }
    }

    val controllersModule = module {
        single { AuthController(get()) }
        single { QuotesController(get()) }
        single { CommentsController(get()) }
        single { SelectionsController(get()) }
        single { ModeratorController(get()) }
        single { AdminController(get()) }
    }

    fun userControllerModule(uploadDir: String) = module {
        single { UserController(get(), uploadDir) }
    }
}
