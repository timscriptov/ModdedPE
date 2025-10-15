package com.mcal.editor.lang.html

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getHtmlSyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // DOCTYPE declaration
        SyntaxPattern(
            Regex("<!DOCTYPE[^>]*>", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_doctype")
        ),
        // Opening tags
        SyntaxPattern(
            Regex("<[\\w:-]+"),
            theme.getLanguageSpecificStyle("html_tag")
        ),
        // Closing tags
        SyntaxPattern(
            Regex("</[\\w:-]+>", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_closing_tag")
        ),
        // Tag endings
        SyntaxPattern(
            Regex(">"),
            theme.getLanguageSpecificStyle("html_tag_end")
        ),
        // Self-closing tags
        SyntaxPattern(
            Regex("/>"),
            theme.getLanguageSpecificStyle("html_self_closing")
        ),
        // Attribute names
        SyntaxPattern(
            Regex("\\s+[\\w:-]+\\s*=", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_attribute_name")
        ),
        // Attribute values
        SyntaxPattern(
            Regex("=\"[^\"]*\"", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_attribute_value")
        ),
        // Single-quoted attribute values
        SyntaxPattern(
            Regex("='[^']*'", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_attribute_value")
        ),
        // Unquoted attribute values
        SyntaxPattern(
            Regex("=[\\w.-]+(?=\\s|>|/)", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_attribute_value")
        ),
        // Boolean attributes (attributes without values)
        SyntaxPattern(
            Regex("\\s+[\\w:-]+(?=\\s|>|/)", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_boolean_attribute")
        ),
        // Comments
        SyntaxPattern(
            Regex("<!--[\\s\\S]*?-->"),
            theme.getCommentStyle()
        ),
        // CDATA sections
        SyntaxPattern(
            Regex("<!\\[CDATA\\[[\\s\\S]*?\\]\\]>", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_cdata")
        ),
        // Script tags
        SyntaxPattern(
            Regex("<script[^>]*>", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_script_tag")
        ),
        // Style tags
        SyntaxPattern(
            Regex("<style[^>]*>", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_style_tag")
        ),
        // Important HTML tags (semantic tags)
        SyntaxPattern(
            Regex("</?(html|head|body|title|meta|link|script|style|div|span|p|h1|h2|h3|h4|h5|h6|a|img|ul|ol|li|table|tr|td|th|form|input|button|select|option|textarea|label|br|hr|nav|header|footer|section|article|aside|main|figure|figcaption|details|summary|mark|time|progress|meter|dialog|datalist|output|canvas|svg|math)", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_semantic_tag")
        ),
        // Entity references
        SyntaxPattern(
            Regex("&[\\w#]+;"),
            theme.getLanguageSpecificStyle("html_entity")
        ),
        // Processing instructions
        SyntaxPattern(
            Regex("<\\?[^>]*\\?>", RegexOption.IGNORE_CASE),
            theme.getLanguageSpecificStyle("html_processing_instruction")
        )
    )
}
