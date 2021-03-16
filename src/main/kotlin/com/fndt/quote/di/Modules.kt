package com.fndt.quote.di

import com.fndt.quote.controllers.AuthController
import com.fndt.quote.controllers.AuthorsController
import com.fndt.quote.controllers.QuotesController
import com.fndt.quote.controllers.RegistrationController
import com.fndt.quote.data.*
import com.fndt.quote.domain.services.implementations.AuthServiceImpl
import com.fndt.quote.domain.services.implementations.RegistrationServiceImpl
import com.fndt.quote.domain.services.AuthService
import com.fndt.quote.domain.services.QuotesEditService
import com.fndt.quote.domain.services.RegistrationService
import org.koin.dsl.module

object Modules {
    val dbModule = module {
        DatabaseProvider.initDb
        single { DatabaseProvider.Quotes }
        single { DatabaseProvider.Users }
        single { DatabaseProvider.Authors }
        single { DatabaseProvider.Tags }
        single { DatabaseProvider.TagsOnQuotes }
        single { DatabaseProvider.Comments }
        single { DatabaseProvider.LikesOnQuotes }
    }
    val serviceModule = module {
    }
    val controllersModule = module {
        single { AuthController(get()) }
        single { AuthorsController(get()) }
        single { QuotesController(get(), get()) }
        single { RegistrationController(get()) }
    }
}
