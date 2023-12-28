package com.idphoto.idphotomaster.feature.editphoto

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.AppTopBar
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons

@Composable
fun EditPhotoScreen(
    modifier: Modifier = Modifier,
    viewModel: EditPhotoViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val splashUiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
        }
    }
    ScreenContent(
        viewState = splashUiState,
        modifier = modifier.fillMaxSize(),
        onBackClick = onBackClick
    )
}

@Composable
private fun ScreenContent(
    viewState: EditPhotoViewState,
    modifier: Modifier,
    onBackClick: () -> Unit
) {
    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                leftIcon = AppIcons.BackIcon,
                onLeftIconClicked = onBackClick
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.White)
                .padding(padding)
                .imePadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            viewState.lastCapturedPhoto?.let { safePhoto ->
                PhotoView(lastCapturedPhoto = safePhoto)
            }
        }
    }
}

@Composable
fun PhotoView(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap
) {
    val capturedPhoto: ImageBitmap =
        remember(lastCapturedPhoto.hashCode()) { lastCapturedPhoto.asImageBitmap() }
    Card(
        modifier = modifier
            .size(256.dp)
            .padding(16.dp)
    ) {
        Image(
            bitmap = capturedPhoto,
            contentDescription = "Last captured photo",
            contentScale = ContentScale.Crop
        )
    }
}