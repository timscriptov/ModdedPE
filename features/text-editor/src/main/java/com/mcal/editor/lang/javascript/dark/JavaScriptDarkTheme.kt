package com.mcal.editor.lang.javascript.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class JavaScriptDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFFCC7832)) // Orange
    override fun getStringStyle() = TextStyle(color = Color(0xFF6A8759)) // Green
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080)) // Gray
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF6897BB)) // Blue
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFA9B7C6)) // Light gray

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "javascript_jsdoc" to TextStyle(color = Color(0xFF629755)),
        "javascript_class" to TextStyle(color = Color(0xFFA9B7C6), fontWeight = FontWeight.Companion.Bold),
        "javascript_function" to TextStyle(color = Color(0xFFFFC66D)),
        "javascript_arrow_function" to TextStyle(color = Color(0xFFFFC66D)),
        "javascript_method" to TextStyle(color = Color(0xFFFFC66D)),
        "javascript_variable" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_constant" to TextStyle(color = Color(0xFF9876AA)),
        "javascript_operator" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_bracket" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_parenthesis" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_brace" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_semicolon" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_parameter" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_property" to TextStyle(color = Color(0xFF9876AA)),
        "javascript_computed_property" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_template_placeholder" to TextStyle(color = Color(0xFF6897BB)),
        "javascript_decorator" to TextStyle(color = Color(0xFFBBB529)),
        "javascript_type" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_generic" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_module" to TextStyle(color = Color(0xFFA9B7C6)),
        "javascript_promise" to TextStyle(color = Color(0xFFCC7832)),
        "javascript_builtin" to TextStyle(color = Color(0xFF9876AA))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFA9B7C6))
    }
}
