package ru.mcal.mclibpatcher.data.repositories

import java.io.File
import java.io.InputStream

class MainRepositoryImpl : MainRepository {
    override fun originalLogoInputStream(): InputStream {
        val inputStream = MainRepository::class.java.getResourceAsStream("title_original.png")
        return inputStream ?: throw IllegalArgumentException("Resource not found: title_original.png")
    }

    override fun patchingMinecraftLib(libraryPath: String, newBytes: ByteArray) {
        originalLogoInputStream().use { inputStream ->
            patchingMinecraftLib(libraryPath, inputStream.readBytes(), newBytes)
        }
    }

    override fun patchingMinecraftLib(libraryPath: String, originalBytes: ByteArray, newBytes: ByteArray) {
        val inputFile = File(libraryPath)
        val libraryBytes = inputFile.readBytes()

        val indexOfOrigImg = hexIndexOf(libraryBytes, originalBytes)
        if (indexOfOrigImg == -1) {
            println("bytes not found in ${inputFile.name}")
            return
        }

        val libraryPatchedBytes = libraryBytes.copyOf()
        System.arraycopy(newBytes, 0, libraryPatchedBytes, indexOfOrigImg, newBytes.size)
        inputFile.writeBytes(libraryPatchedBytes)
    }

    private fun hexIndexOf(source: ByteArray, target: ByteArray): Int {
        outer@ for (i in 0..source.size - target.size) {
            for (j in target.indices) {
                if (source[i + j] != target[j]) {
                    continue@outer
                }
            }
            return i
        }
        return -1
    }
}
