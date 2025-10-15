package com.mcal.editor.lang.javascript.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class JavaScriptLightTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() =
        TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold) // Blue
    override fun getStringStyle() = TextStyle(color = Color(0xFF008000)) // Green
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080)) // Gray
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF098658)) // Dark green
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFF000000)) // Black

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "javascript_jsdoc" to TextStyle(color = Color(0xFF008000)),
        "javascript_class" to TextStyle(color = Color(0xFF267F99), fontWeight = FontWeight.Companion.Bold),
        "javascript_function" to TextStyle(color = Color(0xFF795E26)),
        "javascript_arrow_function" to TextStyle(color = Color(0xFF795E26)),
        "javascript_method" to TextStyle(color = Color(0xFF795E26)),
        "javascript_variable" to TextStyle(color = Color(0xFF001080)),
        "javascript_constant" to TextStyle(color = Color(0xFFAF00DB)),
        "javascript_operator" to TextStyle(color = Color(0xFF000000)),
        "javascript_bracket" to TextStyle(color = Color(0xFF000000)),
        "javascript_parenthesis" to TextStyle(color = Color(0xFF000000)),
        "javascript_brace" to TextStyle(color = Color(0xFF000000)),
        "javascript_semicolon" to TextStyle(color = Color(0xFF000000)),
        "javascript_parameter" to TextStyle(color = Color(0xFF001080)),
        "javascript_property" to TextStyle(color = Color(0xFF001080)),
        "javascript_computed_property" to TextStyle(color = Color(0xFF000000)),
        "javascript_template_placeholder" to TextStyle(color = Color(0xFF0000FF)),
        "javascript_decorator" to TextStyle(color = Color(0xFF800000)),
        "javascript_type" to TextStyle(color = Color(0xFF267F99)),
        "javascript_generic" to TextStyle(color = Color(0xFF000000)),
        "javascript_module" to TextStyle(color = Color(0xFF000000)),
        "javascript_promise" to TextStyle(color = Color(0xFF0000FF)),
        "javascript_builtin" to TextStyle(color = Color(0xFFAF00DB))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFF000000))
    }
}
