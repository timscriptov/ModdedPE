package com.mcal.moddedpe.utils

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

object FileHelper {
    @JvmStatic
    fun readFile(file: File) = file.inputStream().readBytes().toString(StandardCharsets.UTF_8)

    @JvmStatic
    fun readFile(path: String) =
        File(path).inputStream().readBytes().toString(StandardCharsets.UTF_8)

    @JvmStatic
    fun writeToFile(file: File, content: String) = file.writeBytes(
        content.toByteArray(
            StandardCharsets.UTF_8
        )
    )

    @JvmStatic
    fun writeToFile(path: String, content: String) = File(path).writeBytes(
        content.toByteArray(
            StandardCharsets.UTF_8
        )
    )

    @JvmStatic
    fun writeToFile(path: String, content: ByteArray) = File(path).writeBytes(content)

    @JvmStatic
    fun writeToFile(file: File, content: ByteArray) = file.writeBytes(content)

    @JvmStatic
    fun writeToFile(file: File, content: InputStream) =
        content.copyTo(FileOutputStream(file), 1024 * 4)

    fun readFileAsLines(fileName: File): List<String> = fileName.bufferedReader().readLines()
}