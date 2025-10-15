package com.mcal.editor.composition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.mcal.editor.lang.SyntaxPattern
import com.mcal.editor.lang.buildHighlightedCode


@Composable
fun Editor(
    text: String = "",
    patterns: List<SyntaxPattern>,
    backgroundColor: Color = Color.White,
    foregroundColor: Color = Color.Black,
    onValueChange: (text: String) -> Unit,
) {
    val highlightedText = buildHighlightedCode(text, patterns)

    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = foregroundColor, fontFamily = FontFamily.Monospace),
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
        visualTransformation = { text ->
            TransformedText(
                text = highlightedText,
                OffsetMapping.Identity
            )
        },
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .background(backgroundColor)
                    .fillMaxSize()
            ) {
                innerTextField()
            }
        }
    )
}
