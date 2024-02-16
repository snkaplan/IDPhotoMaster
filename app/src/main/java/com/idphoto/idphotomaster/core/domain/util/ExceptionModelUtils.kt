package com.idphoto.idphotomaster.core.domain.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionType

fun ExceptionModel?.getIcon(): ImageVector {
    return when (this?.exceptionType) {
        ExceptionType.NETWORK -> Icons.Default.SignalWifiConnectedNoInternet4
        else -> Icons.Default.Warning
    }
}