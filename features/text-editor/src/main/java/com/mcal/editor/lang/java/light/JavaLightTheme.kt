package com.mcal.editor.lang.java.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class JavaLightTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold)
    override fun getStringStyle() = TextStyle(color = Color(0xFF008000))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF098658))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFF000000))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "java_javadoc" to TextStyle(color = Color(0xFF008000)),
        "java_class" to TextStyle(color = Color(0xFF267F99), fontWeight = FontWeight.Companion.Bold),
        "java_interface" to TextStyle(color = Color(0xFF267F99), fontWeight = FontWeight.Companion.Bold),
        "java_enum" to TextStyle(color = Color(0xFF267F99), fontWeight = FontWeight.Companion.Bold),
        "java_method" to TextStyle(color = Color(0xFF795E26)),
        "java_constructor" to TextStyle(color = Color(0xFF795E26), fontWeight = FontWeight.Companion.Bold),
        "java_annotation" to TextStyle(color = Color(0xFF800000)),
        "java_type" to TextStyle(color = Color(0xFF267F99)),
        "java_generic" to TextStyle(color = Color(0xFF000000)),
        "java_constant" to TextStyle(color = Color(0xFFAF00DB)),
        "java_operator" to TextStyle(color = Color(0xFF000000)),
        "java_bracket" to TextStyle(color = Color(0xFF000000)),
        "java_parenthesis" to TextStyle(color = Color(0xFF000000)),
        "java_brace" to TextStyle(color = Color(0xFF000000)),
        "java_semicolon" to TextStyle(color = Color(0xFF000000)),
        "java_parameter" to TextStyle(color = Color(0xFF001080)),
        "java_local_variable" to TextStyle(color = Color(0xFF001080)),
        "java_field" to TextStyle(color = Color(0xFFAF00DB)),
        "java_lambda" to TextStyle(color = Color(0xFF000000)),
        "java_pattern_matching" to TextStyle(color = Color(0xFF000000)),
        "java_module" to TextStyle(color = Color(0xFF000000)),
        "java_sealed" to TextStyle(color = Color(0xFF000000))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFF000000))
    }
}
