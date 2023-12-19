package com.idphoto.idphotomaster.core.systemdesign.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalDim = compositionLocalOf { Dimensions() }

data class Dimensions(
    val pageMargin: Dp = 20.dp,
)