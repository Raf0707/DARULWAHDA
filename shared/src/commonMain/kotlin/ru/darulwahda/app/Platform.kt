package ru.darulwahda.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform