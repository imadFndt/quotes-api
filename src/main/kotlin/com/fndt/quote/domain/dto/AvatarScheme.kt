package com.fndt.quote.domain.dto

import kotlin.random.Random

enum class AvatarScheme(val fileName: String) {
    PANDA("pand.png"), MONKEY("monk.png"), OWL("owl.png"),
    FOXY("foxy.png"), DEER("deer.png"), BIRB("birb.png"),
    BEMB("bemba.png"), BEER("beer.png"), BEEB("beeb.png"),
    CUSTOM("");

    companion object {
        fun getRandomAvatar(): AvatarScheme = values()[Random.nextInt(values().size - 2)]
    }
}
