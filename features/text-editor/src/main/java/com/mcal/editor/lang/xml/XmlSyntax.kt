package com.mcal.editor.lang.xml

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getXmlSyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // XML Declaration
        SyntaxPattern(
            Regex("<\\?xml[^?>]*\\?>"),
            theme.getLanguageSpecificStyle("xml_declaration")
        ),
        // Opening tags
        SyntaxPattern(
            Regex("<[\\w.:-]+"),
            theme.getLanguageSpecificStyle("xml_tag")
        ),
        // Closing tags
        SyntaxPattern(
            Regex("</[\\w.:-]+>"),
            theme.getLanguageSpecificStyle("xml_closing_tag")
        ),
        // Self-closing tags end
        SyntaxPattern(
            Regex("/>"),
            theme.getLanguageSpecificStyle("xml_self_closing")
        ),
        // Tag endings for opening tags
        SyntaxPattern(
            Regex(">"),
            theme.getLanguageSpecificStyle("xml_tag_end")
        ),
        // Attribute names
        SyntaxPattern(
            Regex("\\s+[\\w.:-]+\\s*="),
            theme.getLanguageSpecificStyle("xml_attribute_name")
        ),
        // Attribute values
        SyntaxPattern(
            Regex("=\"[^\"]*\""),
            theme.getLanguageSpecificStyle("xml_attribute_value")
        ),
        // Single-quoted attribute values
        SyntaxPattern(
            Regex("='[^']*'"),
            theme.getLanguageSpecificStyle("xml_attribute_value")
        ),
        // Comments
        SyntaxPattern(
            Regex("<!--[\\s\\S]*?-->"),
            theme.getCommentStyle()
        ),
        // CDATA sections
        SyntaxPattern(
            Regex("<!\\[CDATA\\[[\\s\\S]*?\\]\\]>"),
            theme.getLanguageSpecificStyle("xml_cdata")
        ),
        // Processing instructions
        SyntaxPattern(
            Regex("<\\?[\\w.:-]+[^?>]*\\?>"),
            theme.getLanguageSpecificStyle("xml_processing_instruction")
        ),
        // Entity references
        SyntaxPattern(
            Regex("&[\\w#]+;"),
            theme.getLanguageSpecificStyle("xml_entity")
        ),
        // DOCTYPE declaration
        SyntaxPattern(
            Regex("<!DOCTYPE[^>]*>"),
            theme.getLanguageSpecificStyle("xml_doctype")
        )
    )
}
