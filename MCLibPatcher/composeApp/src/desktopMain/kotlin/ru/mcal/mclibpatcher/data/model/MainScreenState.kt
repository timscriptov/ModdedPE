package ru.mcal.mclibpatcher.data.model

import java.io.File

data class MainScreenState(
    val isPatching: Boolean = false,
    val libPath: String = "",
    val originalLogoPath: String = "",
    val newLogoPath: String = "",
    val errorMessage: String = "",
) {
    fun validate(): String {
        return if (libPath.isEmpty()) {
            "Please enter path to libminecraftpe.so"
        } else if (!File(libPath).exists()) {
            "$libPath not exists"
        } else if (newLogoPath.isEmpty()) {
            "Please enter path to new logo"
        } else if (!File(newLogoPath).exists()) {
            "$newLogoPath not exists"
        } else if (originalLogoPath.isEmpty()) {
            "Please enter path to original logo"
        } else if (!File(originalLogoPath).exists()) {
            "$newLogoPath not exists"
        } else if (!isValidLogoSize(originalLogoPath, newLogoPath)) {
            "The new logo must have a file size of 87129 bytes or less."
        } else {
            ""
        }
    }

    private fun isValidLogoSize(originalLogo: String, newLogo: String): Boolean {
        return File(newLogo).length() <= File(originalLogo).length()
    }
}
