package com.mcal.core.utils

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

object FileHelper {
    fun readFile(file: File) = file.inputStream().readBytes().toString(StandardCharsets.UTF_8)

    fun readFile(path: String) =
        File(path).inputStream().readBytes().toString(StandardCharsets.UTF_8)

    fun writeToFile(file: File, content: String) = file.writeBytes(
        content.toByteArray(
            StandardCharsets.UTF_8
        )
    )
    fun writeToFile(path: String, content: String) = File(path).writeBytes(
        content.toByteArray(
            StandardCharsets.UTF_8
        )
    )

    fun writeToFile(path: String, content: ByteArray) = File(path).writeBytes(content)

    fun writeToFile(file: File, content: ByteArray) = file.writeBytes(content)

    @JvmStatic
    fun writeToFile(file: File, content: InputStream) = file.writeBytes(readAllBytes(content))

    fun readAllBytes(file: File): ByteArray {
        return readAllBytes(FileInputStream(file))
    }

    fun readAllBytes(inputStream: InputStream): ByteArray {
        val outputStream = ByteArrayOutputStream()
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return outputStream.toByteArray()
    }
    fun readFileAsLines(fileName: File): List<String> = fileName.bufferedReader().readLines()
}