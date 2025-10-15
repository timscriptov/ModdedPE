package com.mcal.editor.lang.cpp.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class CppDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFFCC7832))
    override fun getStringStyle() = TextStyle(color = Color(0xFF6A8759))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF6897BB))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFA9B7C6))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "cpp_preprocessor" to TextStyle(color = Color(0xFFBBB529)),
        "cpp_class" to TextStyle(color = Color(0xFFA9B7C6), fontWeight = FontWeight.Companion.Bold),
        "cpp_function" to TextStyle(color = Color(0xFFFFC66D)),
        "cpp_lambda" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_template" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_namespace" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_type" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_constant" to TextStyle(color = Color(0xFF9876AA)),
        "cpp_operator" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_bracket" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_parenthesis" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_brace" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_semicolon" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_parameter" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_local_variable" to TextStyle(color = Color(0xFFA9B7C6)),
        "cpp_pointer_reference" to TextStyle(color = Color(0xFF9876AA)),
        "cpp_attribute" to TextStyle(color = Color(0xFFBBB529)),
        "cpp_concept" to TextStyle(color = Color(0xFFA9B7C6), fontWeight = FontWeight.Companion.Bold),
        "cpp_module" to TextStyle(color = Color(0xFFA9B7C6))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFA9B7C6))
    }
}
