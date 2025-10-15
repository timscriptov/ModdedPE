package com.mcal.editor.lang.kotlin

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.kotlin.dark.KotlinDarkTheme
import com.mcal.editor.lang.kotlin.light.KotlinLightTheme

enum class KotlinThemes {
    LIGHT,
    DARK,
}

fun getKotlinTheme(theme: KotlinThemes): SyntaxHighlightingTheme {
    return when (theme) {
        KotlinThemes.LIGHT -> KotlinLightTheme()
        KotlinThemes.DARK -> KotlinDarkTheme()
    }
}
