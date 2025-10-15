package com.mcal.editor.lang.xml.dark

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.mcal.editor.lang.SyntaxHighlightingTheme

class XmlDarkTheme : SyntaxHighlightingTheme {
    override fun getKeywordStyle() = TextStyle(color = Color(0xFFCC7832))
    override fun getStringStyle() = TextStyle(color = Color(0xFF6A8759))
    override fun getCommentStyle() = TextStyle(color = Color(0xFF808080))
    override fun getNumbersStyle(): TextStyle = TextStyle(color = Color(0xFF6897BB))
    override fun getDefaultTextStyle(): TextStyle = TextStyle(color = Color(0xFFA9B7C6))

    private val languageSpecificStyles: Map<String, TextStyle> = mapOf(
        "xml_declaration" to TextStyle(color = Color(0xFF808080)),
        "xml_tag" to TextStyle(color = Color(0xFFE8BF6A)),
        "xml_closing_tag" to TextStyle(color = Color(0xFFE8BF6A)),
        "xml_self_closing" to TextStyle(color = Color(0xFFE8BF6A)),
        "xml_tag_end" to TextStyle(color = Color(0xFFE8BF6A)),
        "xml_attribute_name" to TextStyle(color = Color(0xFF9876AA)),
        "xml_attribute_value" to TextStyle(color = Color(0xFF6A8759)),
        "xml_cdata" to TextStyle(color = Color(0xFF6897BB)),
        "xml_processing_instruction" to TextStyle(color = Color(0xFF808080)),
        "xml_entity" to TextStyle(color = Color(0xFF6897BB)),
        "xml_doctype" to TextStyle(color = Color(0xFF808080))
    )

    override fun getLanguageSpecificStyle(tokenType: String): TextStyle {
        return languageSpecificStyles[tokenType] ?: TextStyle(color = Color(0xFFA9B7C6))
    }
}
