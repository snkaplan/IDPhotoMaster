package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun PhotoView(
    modifier: Modifier = Modifier,
    bitmap: ImageBitmap
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.75f)
            .height(350.dp)
    ) {
        Image(
            bitmap = bitmap,
            contentDescription = "Last captured photo",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun CoilImageComponent(
    modifier: Modifier = Modifier,
    imageUrl: String,
    placeholder: Int? = null,
    contentDescription: String? = "Image Content"
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        placeholder = placeholder?.let { painterResource(it) },
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}