package com.idphoto.idphotomaster.core.domain.model.base

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class ExceptionModel(
    var icon: ImageVector? = null,
    var title: String? = null,
    var description: String? = null,
    var primaryButtonText: String? = null,
    var secondaryButtonText: String? = null,
    @StringRes var titleResId: Int? = null,
    @StringRes var descriptionResId: Int? = null,
    @StringRes var primaryButtonTextResId: Int? = null,
    @StringRes var secondaryButtonTextResId: Int? = null,
    var dismissable: Boolean = true,
    val exceptionType: ExceptionType = ExceptionType.GENERAL
)


enum class ExceptionType {
    GENERAL,
    NETWORK,
    REQUIRES_AUTHORIZATION
}
