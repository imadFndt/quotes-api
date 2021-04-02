package com.fndt.quote.domain.filter

enum class QuotesOrder(val key: String) {
    POPULARS("populars"), LATEST("latest");

    companion object {
        fun findKey(key: String): QuotesOrder? {
            values().forEach { if (it.key == key) return it }
            return null
        }
    }
}
