package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel

@Composable
fun ErrorDialog(
    exception: ExceptionModel?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onPrimaryButtonClick: () -> Unit,
    onSecondaryButtonSecondClick: () -> Unit = onDismissRequest
) {
    if (exception != null) {
        Dialog(
            modifier,
            dismissible = exception.dismissable,
            title = exception.titleResId?.let { stringResource(id = it) }
                ?: exception.title.orEmpty(),
            description = exception.descriptionResId?.let { stringResource(id = it) }
                ?: exception.description.orEmpty(),
            primaryButtonText = exception.primaryButtonTextResId?.let { stringResource(id = it) }
                ?: exception.primaryButtonText.orEmpty(),
            secondaryButtonText = exception.secondaryButtonTextResId?.let { stringResource(id = it) }
                ?: exception.secondaryButtonText.orEmpty(),
            primaryButtonClick = onPrimaryButtonClick,
            secondaryButtonClick = onSecondaryButtonSecondClick,
            onDismissRequest = onDismissRequest
        )
    }
}