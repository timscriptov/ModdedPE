package com.mcal.editor.lang.python.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class PythonLightTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold)
    override fun getStringStyle() = TextStyle(color = Color(0xFF008000))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF098658))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFF000000))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "python_class" to TextStyle(color = Color(0xFF267F99), fontWeight = FontWeight.Companion.Bold),
        "python_function" to TextStyle(color = Color(0xFF795E26)),
        "python_method" to TextStyle(color = Color(0xFF795E26)),
        "python_variable" to TextStyle(color = Color(0xFF001080)),
        "python_decorator" to TextStyle(color = Color(0xFF800000)),
        "python_type" to TextStyle(color = Color(0xFF267F99)),
        "python_return_type" to TextStyle(color = Color(0xFF267F99)),
        "python_constant" to TextStyle(color = Color(0xFFAF00DB)),
        "python_builtin" to TextStyle(color = Color(0xFFAF00DB)),
        "python_magic_method" to TextStyle(color = Color(0xFFAF00DB)),
        "python_operator" to TextStyle(color = Color(0xFF000000)),
        "python_bracket" to TextStyle(color = Color(0xFF000000)),
        "python_parenthesis" to TextStyle(color = Color(0xFF000000)),
        "python_brace" to TextStyle(color = Color(0xFF000000)),
        "python_semicolon" to TextStyle(color = Color(0xFF000000)),
        "python_parameter" to TextStyle(color = Color(0xFF001080)),
        "python_self" to TextStyle(color = Color(0xFF001080)),
        "python_class_variable" to TextStyle(color = Color(0xFF001080)),
        "python_import" to TextStyle(color = Color(0xFF000000)),
        "python_exception" to TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold),
        "python_async" to TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold),
        "python_pattern_matching" to TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold),
        "python_fstring" to TextStyle(color = Color(0xFF0000FF))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFF000000))
    }
}
