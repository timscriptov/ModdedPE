package com.mcal.editor.lang.cpp.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class CppLightTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold)
    override fun getStringStyle() = TextStyle(color = Color(0xFF008000))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF098658))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFF000000))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "cpp_preprocessor" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "cpp_class" to TextStyle(color = Color(0xFF267F99), fontWeight = FontWeight.Companion.Bold),
        "cpp_function" to TextStyle(color = Color(0xFF795E26)),
        "cpp_lambda" to TextStyle(color = Color(0xFF000000)),
        "cpp_template" to TextStyle(color = Color(0xFF000000)),
        "cpp_namespace" to TextStyle(color = Color(0xFF000000)),
        "cpp_type" to TextStyle(color = Color(0xFF267F99)),
        "cpp_constant" to TextStyle(color = Color(0xFFAF00DB)),
        "cpp_operator" to TextStyle(color = Color(0xFF000000)),
        "cpp_bracket" to TextStyle(color = Color(0xFF000000)),
        "cpp_parenthesis" to TextStyle(color = Color(0xFF000000)),
        "cpp_brace" to TextStyle(color = Color(0xFF000000)),
        "cpp_semicolon" to TextStyle(color = Color(0xFF000000)),
        "cpp_parameter" to TextStyle(color = Color(0xFF001080)),
        "cpp_local_variable" to TextStyle(color = Color(0xFF001080)),
        "cpp_pointer_reference" to TextStyle(color = Color(0xFFAF00DB)),
        "cpp_attribute" to TextStyle(color = Color(0xFF800000)),
        "cpp_concept" to TextStyle(color = Color(0xFF267F99), fontWeight = FontWeight.Companion.Bold),
        "cpp_module" to TextStyle(color = Color(0xFF000000))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFF000000))
    }
}
