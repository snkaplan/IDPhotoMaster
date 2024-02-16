package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue

private const val DIALOG_WIDTH = 0.80f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorDialog(
    exception: ExceptionModel?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    onButtonSecondClick: () -> Unit = onDismissRequest
) {
    if (exception != null)
        BasicAlertDialog(
            modifier = modifier.fillMaxWidth(DIALOG_WIDTH),
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = exception.dismissable == true,
                dismissOnClickOutside = exception.dismissable == true
            ),
            onDismissRequest = {
                onDismissRequest()
            },
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(vertical = 16.dp, horizontal = 24.dp)
                ) {
                    CloseButtonContainer(
                        hasCloseButton = exception.dismissable == true,
                        onDismissRequest = onDismissRequest
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        exception.icon?.let {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(
                                        shape = CircleShape,
                                        color = BackgroundColor
                                    )
                            ) {
                                Image(
                                    imageVector = it as ImageVector,
                                    contentDescription = "Error Icon",
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        TextAndDescContainer(exception = exception)
                        Spacer(modifier = Modifier.height(24.dp))
                        ButtonContainer(exception, onButtonClick, onButtonSecondClick)
                    }
                }
            }
        }
}

@Composable
private fun CloseButtonContainer(
    hasCloseButton: Boolean,
    onDismissRequest: () -> Unit
) {
    if (hasCloseButton) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            Image(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Button",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
                        onDismissRequest()
                    }
            )
        }
    }
}

@Composable
private fun ButtonContainer(
    exception: ExceptionModel,
    onPrimaryButtonClick: () -> Unit,
    onSecondaryButtonClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        if (exception.secondButtonText.isNullOrEmpty().not() || exception.secondButtonTextResId != null) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.small,
                onClick = {
                    onSecondaryButtonClick.invoke()
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                content = {
                    Text(
                        text = exception.secondButtonTextResId?.let { stringResource(id = it) }
                            ?: exception.secondButtonText.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                })
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (exception.primaryButtonText.isNullOrEmpty().not() || exception.primaryButtonTextResId != null) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                shape = MaterialTheme.shapes.small,
                onClick = { onPrimaryButtonClick.invoke() },
                content = {
                    Text(
                        text = exception.primaryButtonTextResId?.let { stringResource(id = it) }
                            ?: exception.primaryButtonText.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                })
        }
    }
}

@Composable
private fun TextAndDescContainer(modifier: Modifier = Modifier, exception: ExceptionModel) {
    Column(
        modifier = modifier
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = exception.titleResId?.let { stringResource(id = it) }
                ?: exception.title.orEmpty(),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = exception.descriptionResId?.let { stringResource(id = it) }
                ?: exception.description.orEmpty(),
            style = MaterialTheme.typography.bodyMedium.merge(
                TextStyle(lineHeight = 18.sp)
            ),
            color = Color.Black.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorDialogPreview() {
    ErrorDialog(
        exception = ExceptionModel(
            title = "Bir Sorun Oluştu",
            description = "Daha sonra tekrar deneyin",
            primaryButtonText = "Retry",
            secondButtonText = "Vazgeç"
        ),
        onDismissRequest = {},
        onButtonClick = {},
    )
}
