package com.mcal.editor.lang.html.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class HtmlDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFFCC7832))
    override fun getStringStyle() = TextStyle(color = Color(0xFF6A8759))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF6897BB))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFA9B7C6))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "html_doctype" to TextStyle(color = Color(0xFF808080)),
        "html_tag" to TextStyle(color = Color(0xFFE8BF6A)),
        "html_closing_tag" to TextStyle(color = Color(0xFFE8BF6A)),
        "html_tag_end" to TextStyle(color = Color(0xFFE8BF6A)),
        "html_self_closing" to TextStyle(color = Color(0xFFE8BF6A)),
        "html_attribute_name" to TextStyle(color = Color(0xFF9876AA)),
        "html_attribute_value" to TextStyle(color = Color(0xFF6A8759)),
        "html_boolean_attribute" to TextStyle(color = Color(0xFF9876AA)),
        "html_cdata" to TextStyle(color = Color(0xFF6897BB)),
        "html_script_tag" to TextStyle(color = Color(0xFFE8BF6A), fontWeight = FontWeight.Companion.Bold),
        "html_style_tag" to TextStyle(color = Color(0xFFE8BF6A), fontWeight = FontWeight.Companion.Bold),
        "html_semantic_tag" to TextStyle(color = Color(0xFFE8BF6A), fontWeight = FontWeight.Companion.Bold),
        "html_entity" to TextStyle(color = Color(0xFF6897BB)),
        "html_processing_instruction" to TextStyle(color = Color(0xFF808080))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFA9B7C6))
    }
}
