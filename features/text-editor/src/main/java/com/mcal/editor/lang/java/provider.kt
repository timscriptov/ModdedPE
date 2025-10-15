package com.mcal.editor.lang.java

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.java.dark.JavaDarkTheme
import com.mcal.editor.lang.java.light.JavaLightTheme

enum class JavaThemes {
    DARK,
    LIGHT,
}

fun getJavaTheme(theme: JavaThemes): SyntaxHighlightingTheme {
    return when (theme) {
        JavaThemes.DARK -> JavaDarkTheme()
        JavaThemes.LIGHT -> JavaLightTheme()
    }
}
