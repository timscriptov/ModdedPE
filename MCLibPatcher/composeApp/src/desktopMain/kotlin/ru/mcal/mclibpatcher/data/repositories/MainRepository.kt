package ru.mcal.mclibpatcher.data.repositories

import java.io.File
import java.io.InputStream

interface MainRepository {
    fun chooseFile(
        buttonText: String,
        description: String,
        baseDirectory: String,
        onResult: (path: String) -> Unit,
    )
    fun chooseDirectory(
        buttonText: String,
        description: String,
        baseDirectory: String,
        onResult: (path: String) -> Unit,
    )
    fun isValidLogoSize(originalLogo: String, newLogo: String): Boolean
    fun patchingMinecraftLib(libraryPath: String, originalBytes: ByteArray, newBytes: ByteArray)
}
