package com.mcal.editor.lang.cpp

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.cpp.dark.CppDarkTheme
import com.mcal.editor.lang.cpp.light.CppLightTheme

enum class CppThemes {
    DARK,
    LIGHT,
}

fun getCppTheme(theme: CppThemes): SyntaxHighlightingTheme {
    return when (theme) {
        CppThemes.DARK -> CppDarkTheme()
        CppThemes.LIGHT -> CppLightTheme()
    }
}
