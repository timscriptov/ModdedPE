package com.mcal.editor.lang.kotlin.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class KotlinDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFFCC7832))
    override fun getStringStyle() = TextStyle(color = Color(0xFF6A8759))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF6897BB))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFA9B7C6))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "kotlin_kdoc" to TextStyle(color = Color(0xFF629755)),
        "kotlin_class" to TextStyle(color = Color(0xFFA9B7C6), fontWeight = FontWeight.Bold),
        "kotlin_function" to TextStyle(color = Color(0xFFFFC66D)),
        "kotlin_property" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_annotation" to TextStyle(color = Color(0xFFBBB529)),
        "kotlin_type" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_generic" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_constant" to TextStyle(color = Color(0xFF9876AA)),
        "kotlin_operator" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_bracket" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_parenthesis" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_brace" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_semicolon" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_parameter" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_local_variable" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_extension_function" to TextStyle(color = Color(0xFFFFC66D)),
        "kotlin_lambda" to TextStyle(color = Color(0xFFA9B7C6)),
        "kotlin_string_template" to TextStyle(color = Color(0xFF6897BB)),
        "kotlin_control_flow" to TextStyle(color = Color(0xFFCC7832)),
        "kotlin_modifier" to TextStyle(color = Color(0xFFCC7832)),
        "kotlin_builtin_type" to TextStyle(color = Color(0xFFA9B7C6))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFA9B7C6))
    }
}
