package com.idphoto.idphotomaster.core.systemdesign.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.idphoto.idphotomaster.R

object AppIcons {
    val Camera = Icons.Default.Camera
    val CameraOutlined = Icons.Outlined.Camera
    val BackIcon = Icons.Default.KeyboardArrowLeft
    val ProfileSelected = R.drawable.ic_profile_selected
    val ProfileUnselected = R.drawable.ic_profile
}

@Stable
sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}
