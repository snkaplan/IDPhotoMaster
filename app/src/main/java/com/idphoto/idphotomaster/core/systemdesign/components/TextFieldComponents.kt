package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.White

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(15.dp),
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = White,
        unfocusedContainerColor = White,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    ),
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TextField(
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
        trailingIcon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorite") },
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
