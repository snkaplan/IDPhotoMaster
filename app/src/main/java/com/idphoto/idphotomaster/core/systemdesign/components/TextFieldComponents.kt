package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    value: String,
    label: @Composable (() -> Unit),
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        label = label,
        shape = shape,
        onValueChange = onValueChange,
        enabled = enabled,
        colors = colors,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = true,
        placeholder = placeholder,
        supportingText = supportingText,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}

@Composable
@Preview(showBackground = true)
private fun CustomTextFieldPreview() {
    CustomTextField(
        value = "",
        onValueChange = {},
        label = { Text(text = "Label") },
        placeholder = { Text(text = "Placeholder") },
        trailingIcon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
    )
}

@Composable
@Preview(showBackground = true)
private fun CustomTextFieldFillTextPreview() {
    CustomTextField(
        value = "Mail",
        onValueChange = {},
        label = { Text(text = "Mail") },
        placeholder = { Text(text = "Placeholder") },
        isError = true,
        trailingIcon = {},
        supportingText = { Text(text = "Supporting Text") },
    )
}
