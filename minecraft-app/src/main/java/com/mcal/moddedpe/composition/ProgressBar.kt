package com.mcal.moddedpe.composition

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.delay

@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
) {
    val mDefaultSpeed = 0.075f
    var blockDrawingProgress by remember { mutableFloatStateOf(0f) }
    var showBlocks by remember { mutableIntStateOf(1) }
    var isScaling by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            blockDrawingProgress += if (isScaling) {
                mDefaultSpeed / 2
            } else {
                mDefaultSpeed
            }

            if (blockDrawingProgress >= 1 && !isScaling) {
                blockDrawingProgress = 0f
                showBlocks++
                if (showBlocks > 4) {
                    showBlocks = 1
                    isScaling = true
                }
            } else if (blockDrawingProgress >= 0.5 && isScaling) {
                isScaling = false
                blockDrawingProgress = 0f
                showBlocks = 2
            }
        }
    }

    Canvas(modifier = modifier) {
        when (showBlocks) {
            1 -> {
                drawBlock(0f, (size.height * blockDrawingProgress).toInt().toFloat(), size.width, size.height)
            }

            2 -> {
                drawBlock(0f, size.height / 2, size.width / 2, size.height)
                val blockDrawHeight = (size.height / 2 * blockDrawingProgress).toInt().toFloat()
                drawBlock(size.width / 2, blockDrawHeight, size.width, blockDrawHeight + (size.height / 2))
            }

            3 -> {
                drawBlock(0f, size.height / 2, size.width, size.height)
                val blockDrawHeight = (size.height / 2 * blockDrawingProgress).toInt().toFloat()
                drawBlock(0f, 0f, size.width / 2, blockDrawHeight + 1)
            }

            4 -> {
                drawBlock(0f, size.height / 2, size.width, size.height)
                drawBlock(0f, 0f, size.width / 2, size.height / 2)
                val blockDrawHeight = (size.height / 2 * blockDrawingProgress).toInt().toFloat()
                drawBlock(size.width / 2, 0f, size.width, blockDrawHeight + 1)
            }
        }
    }
}

private fun DrawScope.drawBlock(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    color: Color = Color(0xFF0050CB)
) {
    drawRect(
        color = color,
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(right - left, bottom - top)
    )
}
