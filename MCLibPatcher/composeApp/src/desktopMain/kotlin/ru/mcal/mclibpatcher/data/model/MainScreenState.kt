package ru.mcal.mclibpatcher.data.model

data class MainScreenState(
    val isPatching: Boolean = false,
    val libPath: String = "",
    val originalLogoPath: String = "",
    val logoPath: String = "",
)
