package com.idphoto.idphotomaster.core.systemdesign.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Camera
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

object AppIcons {
    val Camera = Icons.Default.Camera
    val BackIcon = Icons.AutoMirrored.Default.KeyboardArrowLeft
}

@Stable
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector, val contentDescription: String) :
        Icon()

    data class DrawableResourceIcon(@DrawableRes val id: Int, val contentDescription: String) :
        Icon()
}
