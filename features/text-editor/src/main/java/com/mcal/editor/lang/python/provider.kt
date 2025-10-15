package com.mcal.editor.lang.python

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.python.dark.PythonDarkTheme
import com.mcal.editor.lang.python.light.PythonLightTheme

enum class PythonThemes {
    DARK,
    LIGHT,
}

fun getPythonTheme(theme: PythonThemes): SyntaxHighlightingTheme {
    return when (theme) {
        PythonThemes.DARK -> PythonDarkTheme()
        PythonThemes.LIGHT -> PythonLightTheme()
    }
}
