package com.mcal.editor.lang.html.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class HtmlLightTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFF0000FF), fontWeight = FontWeight.Companion.Bold)
    override fun getStringStyle() = TextStyle(color = Color(0xFF008000))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF098658))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFF000000))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "html_doctype" to TextStyle(color = Color(0xFF808080)),
        "html_tag" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "html_closing_tag" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "html_tag_end" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "html_self_closing" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "html_attribute_name" to TextStyle(color = Color(0xFFD35400)),
        "html_attribute_value" to TextStyle(color = Color(0xFF008000)),
        "html_boolean_attribute" to TextStyle(color = Color(0xFFD35400)),
        "html_cdata" to TextStyle(color = Color(0xFF0000FF)),
        "html_script_tag" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "html_style_tag" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "html_semantic_tag" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "html_entity" to TextStyle(color = Color(0xFF0000FF)),
        "html_processing_instruction" to TextStyle(color = Color(0xFF808080))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFF000000))
    }
}
