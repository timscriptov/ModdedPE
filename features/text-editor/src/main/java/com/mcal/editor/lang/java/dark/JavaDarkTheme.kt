package com.mcal.editor.lang.java.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class JavaDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFFCC7832))
    override fun getStringStyle() = TextStyle(color = Color(0xFF6A8759))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF6897BB))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFA9B7C6))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "java_javadoc" to TextStyle(color = Color(0xFF629755)),
        "java_class" to TextStyle(color = Color(0xFFA9B7C6), fontWeight = FontWeight.Companion.Bold),
        "java_interface" to TextStyle(color = Color(0xFFA9B7C6), fontWeight = FontWeight.Companion.Bold),
        "java_enum" to TextStyle(color = Color(0xFFA9B7C6), fontWeight = FontWeight.Companion.Bold),
        "java_method" to TextStyle(color = Color(0xFFFFC66D)),
        "java_constructor" to TextStyle(color = Color(0xFFFFC66D), fontWeight = FontWeight.Companion.Bold),
        "java_annotation" to TextStyle(color = Color(0xFFBBB529)),
        "java_type" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_generic" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_constant" to TextStyle(color = Color(0xFF9876AA)),
        "java_operator" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_bracket" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_parenthesis" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_brace" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_semicolon" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_parameter" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_local_variable" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_field" to TextStyle(color = Color(0xFF9876AA)),
        "java_lambda" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_pattern_matching" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_module" to TextStyle(color = Color(0xFFA9B7C6)),
        "java_sealed" to TextStyle(color = Color(0xFFA9B7C6))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFA9B7C6))
    }
}
