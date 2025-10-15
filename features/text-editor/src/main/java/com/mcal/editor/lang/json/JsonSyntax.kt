package com.mcal.editor.lang.json

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getJsonSyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // JSON keywords (true, false, null)
        SyntaxPattern(
            Regex("\\b(true|false|null)\\b"),
            theme.getKeywordStyle()
        ),
        // Strings (keys and values)
        SyntaxPattern(
            Regex("\"(?:[^\"\\\\]|\\\\.)*\""),
            theme.getStringStyle()
        ),
        // Numbers (integers and floats with scientific notation)
        SyntaxPattern(
            Regex("-?\\b(?:[0-9]+(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?|0x[0-9a-fA-F]+)\\b"),
            theme.getNumbersStyle()
        ),
        // Object keys (strings followed by colon)
        SyntaxPattern(
            Regex("\"(?:[^\"\\\\]|\\\\.)*\"\\s*:"),
            theme.getLanguageSpecificStyle("json_key")
        ),
        // Braces
        SyntaxPattern(
            Regex("[{}]"),
            theme.getLanguageSpecificStyle("json_brace")
        ),
        // Brackets
        SyntaxPattern(
            Regex("[\\[\\]]"),
            theme.getLanguageSpecificStyle("json_bracket")
        ),
        // Colon (standalone)
        SyntaxPattern(
            Regex(":"),
            theme.getLanguageSpecificStyle("json_colon")
        ),
        // Comma
        SyntaxPattern(
            Regex(","),
            theme.getLanguageSpecificStyle("json_comma")
        ),
        // Escape sequences in strings
        SyntaxPattern(
            Regex("\\\\(?:[\"\\\\/bfnrt]|u[0-9a-fA-F]{4})"),
            theme.getLanguageSpecificStyle("json_escape")
        )
    )
}
