package com.mcal.editor.lang.html

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.html.dark.HtmlDarkTheme
import com.mcal.editor.lang.html.light.HtmlLightTheme

enum class HtmlThemes {
    DARK,
    LIGHT,
}

fun getHtmlTheme(theme: HtmlThemes): SyntaxHighlightingTheme {
    return when (theme) {
        HtmlThemes.DARK -> HtmlDarkTheme()
        HtmlThemes.LIGHT -> HtmlLightTheme()
    }
}
