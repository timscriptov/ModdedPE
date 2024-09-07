package ru.mcal.mclibpatcher.data.repositories

import java.io.InputStream

interface MainRepository {
    fun originalLogoInputStream(): InputStream
    fun patchingMinecraftLib(libraryPath: String, newBytes: ByteArray)
    fun patchingMinecraftLib(libraryPath: String, originalBytes: ByteArray, newBytes: ByteArray)
}
