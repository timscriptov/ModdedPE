package com.mcal.editor.lang

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle

data class SyntaxPattern(val regex: Regex, val style: TextStyle)

interface SyntaxHighlightingTheme {
    fun getKeywordStyle(): TextStyle
    fun getStringStyle(): TextStyle
    fun getCommentStyle(): TextStyle
    fun getNumbersStyle(): TextStyle
    fun getDefaultTextStyle(): TextStyle

    fun getLanguageSpecificStyle(tokenType: String): TextStyle
}

fun buildHighlightedCode(
    code: String,
    patterns: List<SyntaxPattern>
): AnnotatedString {

    val builder = AnnotatedString.Builder()
    var currentIndex = 0

    while (currentIndex < code.length) {
        var matchFound = false

        // Apply the first matching pattern
        for (pattern in patterns) {
            val match = pattern.regex.find(code, currentIndex)
            if (match != null && match.range.first == currentIndex) {
                builder.pushStyle(pattern.style.toSpanStyle())
                builder.append(match.value)
                builder.pop()
                currentIndex += match.value.length
                matchFound = true
                break
            }
        }

        // If no match, append the character as plain text
        if (!matchFound) {
            builder.append(code[currentIndex])
            currentIndex++
        }
    }

    return builder.toAnnotatedString()
}
