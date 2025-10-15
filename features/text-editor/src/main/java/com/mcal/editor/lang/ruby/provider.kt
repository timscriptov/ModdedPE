package com.mcal.editor.lang.ruby
import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.ruby.dark.RubyDarkTheme
import com.mcal.editor.lang.ruby.light.RubyDefaultTheme

enum class RubyThemes  {
    LIGHT,
    DARK,
}

fun getRubyTheme(theme: RubyThemes) : SyntaxHighlightingTheme {
    return when (theme) {
        RubyThemes.LIGHT -> RubyDefaultTheme()
        // not yet implemented
        RubyThemes.DARK -> RubyDarkTheme()
    }
}
