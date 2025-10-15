package com.mcal.editor.lang.ruby.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class RubyDefaultTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xff399bc5))
    override fun getStringStyle() = TextStyle(color = Color(0xff007a09))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF888888), fontWeight = FontWeight.Light)
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xff007a09))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFFFFFFF))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "ruby_class" to TextStyle(color = Color(0xFFA9E2F3), fontWeight = FontWeight.Bold),
        "ruby_method" to TextStyle(color = Color(0xFFF781F3)),
        "ruby_variable" to TextStyle(color = Color.DarkGray),
        "ruby_symbol" to TextStyle(color = Color(0xffc6a83b), fontWeight = FontWeight.Bold),
        "ruby_constant" to TextStyle(color = Color(0xFFF5A9D0), fontWeight = FontWeight.Bold),
        "ruby_operator" to TextStyle(color = Color.Magenta),
        "ruby_bracket" to TextStyle(color = Color.DarkGray),
        "ruby_parenthesis" to TextStyle(color = Color.DarkGray),
        "ruby_brace" to TextStyle(color = Color.DarkGray),
        "ruby_parameter" to TextStyle(color = Color(0xFFFD971F)),
        "ruby_local_variable" to TextStyle(color = Color.DarkGray),
        "ruby_global_variable" to TextStyle(color = Color.DarkGray),
        "ruby_instance_variable" to TextStyle(color = Color(0xFFA6E22E)),
        "ruby_class_variable" to TextStyle(color = Color(0xFFA6E22E)),
        "ruby_builtin_function" to TextStyle(color = Color(0xFFCC7832))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFFFFFFF))
    }
}
