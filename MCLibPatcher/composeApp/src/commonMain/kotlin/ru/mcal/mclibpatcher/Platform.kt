package ru.mcal.mclibpatcher

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform