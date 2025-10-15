package com.mcal.editor.lang.ruby.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class RubyDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xff5ebfdd)) // Brighter blue
    override fun getStringStyle() = TextStyle(color = Color(0xff4caf50)) // Brighter green
    override fun getCommentStyle() = TextStyle(color = Color(0xFF757575), fontWeight = FontWeight.Light) // Lighter gray
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xff4caf50)) // Brighter green
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFE0E0E0)) // Light gray for default text

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "ruby_class" to TextStyle(color = Color(0xFF80DEEA), fontWeight = FontWeight.Bold), // Brighter cyan
        "ruby_method" to TextStyle(color = Color(0xFFF48FB1)), // Brighter pink
        "ruby_variable" to TextStyle(color = Color(0xFFB0BEC5)), // Light gray
        "ruby_symbol" to TextStyle(color = Color(0xffffcc80), fontWeight = FontWeight.Bold), // Brighter yellow
        "ruby_constant" to TextStyle(color = Color(0xFFF8BBD0), fontWeight = FontWeight.Bold), // Brighter pink
        "ruby_operator" to TextStyle(color = Color(0xFFCE93D8)), // Brighter magenta
        "ruby_bracket" to TextStyle(color = Color(0xFFB0BEC5)), // Light gray
        "ruby_parenthesis" to TextStyle(color = Color(0xFFB0BEC5)), // Light gray
        "ruby_brace" to TextStyle(color = Color(0xFFB0BEC5)), // Light gray
        "ruby_parameter" to TextStyle(color = Color(0xFFFFAB91)), // Brighter orange
        "ruby_local_variable" to TextStyle(color = Color(0xFFB0BEC5)), // Light gray
        "ruby_global_variable" to TextStyle(color = Color(0xFFB0BEC5)), // Light gray
        "ruby_instance_variable" to TextStyle(color = Color(0xFFC5E1A5)), // Brighter green
        "ruby_class_variable" to TextStyle(color = Color(0xFFC5E1A5)), // Brighter green
        "ruby_builtin_function" to TextStyle(color = Color(0xFFCC7832))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFE0E0E0)) // Light gray for unknown token types
    }
}
