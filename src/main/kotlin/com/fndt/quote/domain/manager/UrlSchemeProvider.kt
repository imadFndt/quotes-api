package com.fndt.quote.domain.manager

object UrlSchemeProvider {
    lateinit var scheme: String
        private set

    fun initScheme(host: String) {
        scheme = "http://$host/images/"
    }
}
