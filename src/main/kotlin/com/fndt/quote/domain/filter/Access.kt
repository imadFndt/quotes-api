package com.fndt.quote.domain.filter

enum class Access(val key: String) {
    PRIVATE("private"), PUBLIC("public"), ALL("all");

    companion object {
        fun findKey(key: String): Access? {
            values().forEach { if (it.key == key) return it }
            return null
        }
    }
}
