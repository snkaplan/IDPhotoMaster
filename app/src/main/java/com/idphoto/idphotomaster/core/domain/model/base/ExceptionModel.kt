package com.idphoto.idphotomaster.core.domain.model.base

import androidx.annotation.StringRes

data class ExceptionModel(
    var icon: Any? = null,
    var title: String? = null,
    var description: String? = null,
    var primaryButtonText: String? = null,
    var secondButtonText: String? = null,
    @StringRes var titleResId: Int? = null,
    @StringRes var descriptionResId: Int? = null,
    @StringRes var primaryButtonTextResId: Int? = null,
    @StringRes var secondButtonTextResId: Int? = null,
    var dismissable: Boolean? = true,
    val exceptionType: ExceptionType = ExceptionType.GENERAL
)


enum class ExceptionType {
    GENERAL,
    NETWORK
}
