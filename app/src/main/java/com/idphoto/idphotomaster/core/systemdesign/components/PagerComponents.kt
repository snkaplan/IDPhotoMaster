package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotIndicator(
    count: Int,
    pagerState: PagerState,
) {
    val circleSpacing = 8.dp
    val circleSize = 20.dp
    val innerCircle = 14.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp), contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier) {
            val distance = (circleSize + circleSpacing).toPx()

            val centerX = size.width / 2
            val centerY = size.height / 2

            val totalWidth = distance * count
            val startX = centerX - (totalWidth / 2) + (circleSize / 2).toPx()

            repeat(count) {
                val pageOffset = pagerState.calculateCurrentOffsetForPage(it)
                val alpha = 0.3f.coerceAtLeast(1 - pageOffset.absoluteValue)
                val scale = 1f.coerceAtMost(pageOffset.absoluteValue)
                val x = startX + (it * distance)
                val circleCenter = Offset(x, centerY)
                val radius = circleSize.toPx() / 2
                val innerRadius = (innerCircle.toPx() * scale) / 1.5f
                drawCircle(
                    color = Color.Black, center = circleCenter,
                    radius = radius, alpha = alpha,
                )
                drawCircle(color = Color.White, center = circleCenter, radius = innerRadius)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}
