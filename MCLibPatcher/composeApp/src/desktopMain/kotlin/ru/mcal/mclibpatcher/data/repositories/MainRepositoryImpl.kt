package ru.mcal.mclibpatcher.data.repositories

import java.io.File
import javax.swing.JFileChooser

class MainRepositoryImpl : MainRepository {
    override fun chooseFile(
        buttonText: String,
        description: String,
        baseDirectory: String,
        onResult: (path: String) -> Unit,
    ) {
        val fileChooser = JFileChooser(baseDirectory).apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            dialogTitle = description
            approveButtonText = buttonText
            approveButtonToolTipText = description
        }
        fileChooser.showOpenDialog(null)
        val result = fileChooser.selectedFile
        onResult(
            if (result != null && result.exists()) {
                result.absolutePath.toString()
            } else {
                ""
            }
        )
    }

    override fun chooseDirectory(
        buttonText: String,
        description: String,
        baseDirectory: String,
        onResult: (path: String) -> Unit,
    ) {
        val fileChooser = JFileChooser(baseDirectory).apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = description
            approveButtonText = buttonText
            approveButtonToolTipText = description
        }
        fileChooser.showOpenDialog(null)
        val result = fileChooser.selectedFile
        onResult(
            if (result != null && result.exists()) {
                result.absolutePath.toString()
            } else {
                ""
            }
        )
    }

    override suspend fun patchingMinecraftLib(libraryPath: String, originalBytes: ByteArray, newBytes: ByteArray) {
        val inputFile = File(libraryPath)
        val outputFile = File(libraryPath.replace(".so", "_patch.so"))
        val libraryBytes = inputFile.readBytes()
        val originalLogoSize = originalBytes.size
        val newLogoSize = newBytes.size
        val newLogoBytes = if (newLogoSize < originalLogoSize) {
            newBytes + "\u0000".repeat(originalLogoSize - newLogoSize).toByteArray()
        } else {
            newBytes
        }

        val indexOfOrigImg = hexIndexOf(libraryBytes, originalBytes)
        if (indexOfOrigImg == -1) {
            println("bytes not found in ${inputFile.name}")
            return
        }

        val libraryPatchedBytes = libraryBytes.copyOf()
        System.arraycopy(newLogoBytes, 0, libraryPatchedBytes, indexOfOrigImg, newLogoBytes.size)
        outputFile.writeBytes(libraryPatchedBytes)
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
