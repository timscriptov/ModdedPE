package com.mcal.editor.lang.json.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class JsonDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFFCC7832)) // Orange
    override fun getStringStyle() = TextStyle(color = Color(0xFF6A8759)) // Green
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080)) // Gray
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF6897BB)) // Blue
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFA9B7C6)) // Light gray

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "json_key" to TextStyle(color = Color(0xFF9876AA), fontWeight = FontWeight.Companion.Bold), // Purple for keys
        "json_brace" to TextStyle(color = Color(0xFFA9B7C6)), // Light gray for braces
        "json_bracket" to TextStyle(color = Color(0xFFA9B7C6)), // Light gray for brackets
        "json_colon" to TextStyle(color = Color(0xFFA9B7C6)), // Light gray for colon
        "json_comma" to TextStyle(color = Color(0xFFA9B7C6)), // Light gray for comma
        "json_escape" to TextStyle(color = Color(0xFFCC7832)) // Orange for escape sequences
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFA9B7C6))
    }
}
