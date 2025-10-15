package com.mcal.editor.lang.kotlin.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class KotlinLightTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Bold)
    override fun getStringStyle() = TextStyle(color = Color(0xFF008000))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF098658))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFF000000))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "kotlin_kdoc" to TextStyle(color = Color(0xFF008000)),
        "kotlin_class" to TextStyle(color = Color(0xFF267F99), fontWeight = FontWeight.Bold),
        "kotlin_function" to TextStyle(color = Color(0xFF795E26)),
        "kotlin_property" to TextStyle(color = Color(0xFF001080)),
        "kotlin_annotation" to TextStyle(color = Color(0xFF800000)),
        "kotlin_type" to TextStyle(color = Color(0xFF267F99)),
        "kotlin_generic" to TextStyle(color = Color(0xFF000000)),
        "kotlin_constant" to TextStyle(color = Color(0xFFAF00DB)),
        "kotlin_operator" to TextStyle(color = Color(0xFF000000)),
        "kotlin_bracket" to TextStyle(color = Color(0xFF000000)),
        "kotlin_parenthesis" to TextStyle(color = Color(0xFF000000)),
        "kotlin_brace" to TextStyle(color = Color(0xFF000000)),
        "kotlin_semicolon" to TextStyle(color = Color(0xFF000000)),
        "kotlin_parameter" to TextStyle(color = Color(0xFF001080)),
        "kotlin_local_variable" to TextStyle(color = Color(0xFF001080)),
        "kotlin_extension_function" to TextStyle(color = Color(0xFF795E26)),
        "kotlin_lambda" to TextStyle(color = Color(0xFF000000)),
        "kotlin_string_template" to TextStyle(color = Color(0xFF0000FF)),
        "kotlin_control_flow" to TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Bold),
        "kotlin_modifier" to TextStyle(color = Color(0xFF0000FF)),
        "kotlin_builtin_type" to TextStyle(color = Color(0xFF267F99))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFF000000))
    }
}
