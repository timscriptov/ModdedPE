package com.mcal.editor.lang.json.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class JsonLightTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() =
        TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold) // Blue
    override fun getStringStyle() = TextStyle(color = Color(0xFF008000)) // Green
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080)) // Gray
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF098658)) // Dark green
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFF000000)) // Black

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "json_key" to TextStyle(color = Color(0xFFAF00DB), fontWeight = FontWeight.Companion.Bold), // Purple for keys
        "json_brace" to TextStyle(color = Color(0xFF000000)), // Black for braces
        "json_bracket" to TextStyle(color = Color(0xFF000000)), // Black for brackets
        "json_colon" to TextStyle(color = Color(0xFF000000)), // Black for colon
        "json_comma" to TextStyle(color = Color(0xFF000000)), // Black for comma
        "json_escape" to TextStyle(color = Color(0xFF0000FF)) // Blue for escape sequences
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFF000000))
    }
}
