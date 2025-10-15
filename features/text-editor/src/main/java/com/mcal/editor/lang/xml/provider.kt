package com.mcal.editor.lang.xml

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.xml.dark.XmlDarkTheme
import com.mcal.editor.lang.xml.light.XmlLightTheme

enum class XmlThemes {
    DARK,
    LIGHT,
}

fun getXmlTheme(theme: XmlThemes): SyntaxHighlightingTheme {
    return when (theme) {
        XmlThemes.DARK -> XmlDarkTheme()
        XmlThemes.LIGHT -> XmlLightTheme()
    }
}
