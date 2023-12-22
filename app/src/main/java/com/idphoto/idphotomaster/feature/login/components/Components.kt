package com.idphoto.idphotomaster.feature.login.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.idphoto.idphotomaster.core.systemdesign.components.CustomTextField
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.LightGrey

@Composable
fun UserInputTextField(
    value: String,
    errorMessageRes: Int?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes placeholder: Int,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    CustomTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = value,
        onValueChange = onValueChange,
        isError = errorMessageRes != null,
        enabled = enabled,
        trailingIcon = {
            AnimatedVisibility(visible = value.isNotEmpty() && enabled) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onValueChange("")
                        },
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = "",
                )
            }
        },
        supportingText = {
            AnimatedVisibility(visible = errorMessageRes != null) {
                val errorMessage = errorMessageRes?.let {
                    stringResource(id = it)
                } ?: ""
                Text(text = errorMessage)
            }
        },
        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        label = {
            Text(
                text = stringResource(placeholder),
                style = TextStyle(fontSize = 14.sp, color = LightGrey)
            )
        },
    )
}

@Composable
fun PasswordTextField(
    value: String,
    errorMessageRes: Int?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes placeholder: Int,
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    CustomTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        isError = errorMessageRes != null,
        trailingIcon = {
            AnimatedVisibility(visible = value.isNotEmpty() && enabled) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            isPasswordVisible = !isPasswordVisible
                        },
                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "",
                )
            }
        },
        supportingText = {
            AnimatedVisibility(visible = errorMessageRes != null) {
                val errorMessage = errorMessageRes?.let {
                    stringResource(id = it)
                } ?: ""

                Text(text = errorMessage)
            }
        },
        keyboardActions = keyboardActions,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        label = {
            Text(
                text = stringResource(id = placeholder),
                style = TextStyle(fontSize = 14.sp, color = LightGrey)
            )
        },
    )
}