package com.mcal.moddedpe.utils

import com.mcal.apkparser.zip.ZipFile
import java.io.File
import java.io.FileOutputStream

object ZipHelper {
    fun getZipEntry(input: File, entry: String): ByteArray? {
        ZipFile(input).use { zipFile ->
            val zipEntry = zipFile.getEntry(entry) ?: return null
            zipFile.getInputStream(zipEntry).use { inputStream ->
                return inputStream.readBytes()
            }
        }
    }

    fun extractFiles(input: File, output: File) {
        ZipFile(input).use { zipFile ->
            val enumeration = zipFile.entries
            while (enumeration.hasMoreElements()) {
                val ze = enumeration.nextElement()
                val file = File(output, ze.name)
                if (ze.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile?.mkdirs()
                    zipFile.getInputStream(ze).use { inputStream ->
                        FileOutputStream(file).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
        }
    }
}
