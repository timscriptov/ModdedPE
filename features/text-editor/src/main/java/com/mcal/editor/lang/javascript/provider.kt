package com.mcal.editor.lang.javascript

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.javascript.dark.JavaScriptDarkTheme
import com.mcal.editor.lang.javascript.light.JavaScriptLightTheme

enum class JavaScriptThemes {
    DARK,
    LIGHT,
}

fun getJavaScriptTheme(theme: JavaScriptThemes): SyntaxHighlightingTheme {
    return when (theme) {
        JavaScriptThemes.DARK -> JavaScriptDarkTheme()
        JavaScriptThemes.LIGHT -> JavaScriptLightTheme()
    }
}
