package com.mcal.editor.lang.json

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.json.dark.JsonDarkTheme
import com.mcal.editor.lang.json.light.JsonLightTheme

enum class JsonThemes {
    DARK,
    LIGHT,
}

fun getJsonTheme(theme: JsonThemes): SyntaxHighlightingTheme {
    return when (theme) {
        JsonThemes.DARK -> JsonDarkTheme()
        JsonThemes.LIGHT -> JsonLightTheme()
    }
}
