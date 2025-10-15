package com.mcal.editor.lang.xml.light

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.mcal.editor.lang.SyntaxHighlightingTheme

class XmlLightTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFF0000FF))
    override fun getStringStyle() = TextStyle(color = Color(0xFF008000))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF098658))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFF000000))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "xml_declaration" to TextStyle(color = Color(0xFF808080)),
        "xml_tag" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "xml_closing_tag" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "xml_self_closing" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "xml_tag_end" to TextStyle(color = Color(0xFF800000), fontWeight = FontWeight.Companion.Bold),
        "xml_attribute_name" to TextStyle(color = Color(0xFFAF00DB)),
        "xml_attribute_value" to TextStyle(color = Color(0xFF008000)),
        "xml_cdata" to TextStyle(color = Color(0xFF0000FF)),
        "xml_processing_instruction" to TextStyle(color = Color(0xFF808080)),
        "xml_entity" to TextStyle(color = Color(0xFF0000FF)),
        "xml_doctype" to TextStyle(color = Color(0xFF808080))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFF000000))
    }
}
