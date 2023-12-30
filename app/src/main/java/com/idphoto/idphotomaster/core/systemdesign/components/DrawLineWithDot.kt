package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DrawLineWithDot(modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val halfScreenWidth = canvasWidth / 2f
        drawLine(
            color = Color.Black,
            start = Offset(0f, canvasHeight / 2f),
            end = Offset(halfScreenWidth - 50, canvasHeight / 2f),
            strokeWidth = 5f
        )

        drawCircle(
            color = Color.Black,
            center = Offset(canvasWidth / 2f, canvasHeight / 2f),
            radius = 8.dp.toPx()
        )

        drawLine(
            color = Color.Black,
            start = Offset(halfScreenWidth + 50, canvasHeight / 2f),
            end = Offset(canvasWidth, canvasHeight / 2f),
            strokeWidth = 5f
        )
    }
}