package com.mcal.editor.lang.python.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class PythonDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFFCC7832))
    override fun getStringStyle() = TextStyle(color = Color(0xFF6A8759))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF6897BB))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFA9B7C6))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "python_class" to TextStyle(color = Color(0xFFA9B7C6), fontWeight = FontWeight.Companion.Bold),
        "python_function" to TextStyle(color = Color(0xFFFFC66D)),
        "python_method" to TextStyle(color = Color(0xFFFFC66D)),
        "python_variable" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_decorator" to TextStyle(color = Color(0xFFBBB529)),
        "python_type" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_return_type" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_constant" to TextStyle(color = Color(0xFF9876AA)),
        "python_builtin" to TextStyle(color = Color(0xFF9876AA)),
        "python_magic_method" to TextStyle(color = Color(0xFF9876AA)),
        "python_operator" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_bracket" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_parenthesis" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_brace" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_semicolon" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_parameter" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_self" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_class_variable" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_import" to TextStyle(color = Color(0xFFA9B7C6)),
        "python_exception" to TextStyle(color = Color(0xFFCC7832)),
        "python_async" to TextStyle(color = Color(0xFFCC7832)),
        "python_pattern_matching" to TextStyle(color = Color(0xFFCC7832)),
        "python_fstring" to TextStyle(color = Color(0xFF6897BB))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFA9B7C6))
    }
}
