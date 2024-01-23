package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor

@Composable
fun LoadingView(modifier: Modifier = Modifier, backgroundColor: Color = BackgroundColor.copy(alpha = 0.5f)) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}