package com.mcal.editor.composition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp)
    ) {
        Row {
            val highlightedText = buildHighlightedCode(text, patterns)
            val lines = text.lines().ifEmpty { listOf("") }

            LineNumbersColumn(
                lines = lines,
            )

            BasicTextField(
                value = text,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = foregroundColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                visualTransformation = { text ->
                    TransformedText(
                        text = highlightedText,
                        OffsetMapping.Identity
                    )
                },
                decorationBox = { innerTextField ->
                    Box(
                        Modifier
                            .background(backgroundColor)
                            .fillMaxWidth()
                    ) {
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun LineNumbersColumn(
    lines: List<String>,
) {
    Column(
        modifier = Modifier
            .width(50.dp)
    ) {
        lines.forEachIndexed { index, _ ->
            Text(
                text = "${index + 1}",
                style = TextStyle(
                    fontSize = 16.sp,
                ),
                modifier = Modifier
                    .height(24.dp)
                    .padding(2.dp)
            )
        }
    }
}
