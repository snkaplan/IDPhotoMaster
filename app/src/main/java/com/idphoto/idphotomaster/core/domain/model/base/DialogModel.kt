package com.idphoto.idphotomaster.core.domain.model.base

import androidx.compose.ui.graphics.vector.ImageVector

data class DialogModel(
    val title: String,
    val description: String,
    val confirmText: String,
    val dismissText: String?,
    val icon: ImageVector? = null,
    val confirmCallback: (() -> Unit)?,
    val onDismissCallback: (() -> Unit)?
)