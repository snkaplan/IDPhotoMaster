package com.idphoto.idphotomaster.feature.login.components

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.data.util.GoogleSignInHelper
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

@Preview
@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    text: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    onSuccess: (String) -> Unit = {},
    onError: (String?) -> Unit = {}
) {
    val context = LocalContext.current

    // Instance of GoogleSignInClient and BeginSignInRequest
    val client = remember { GoogleSignInHelper.getGoogleSignInClient(context) }
    val request = remember { GoogleSignInHelper.getGoogleSignInRequest() }

    // Result Launcher to handle Sign In
    val signInResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val credential = client.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken

            if (idToken != null) {
                onSuccess(idToken)
            } else {
                onError(context.getString(R.string.error_google_sign_in))
            }
        }
    }

    Button(
        enabled = enabled,
        onClick = {
            // Side effect on click (optional)
            onClick()
            // Sign in with Google
            client.beginSignIn(request).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intentSender = task.result.pendingIntent.intentSender
                    val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                    signInResultLauncher.launch(intentSenderRequest)
                } else {
                    onError(context.getString(R.string.error_google_sign_in))
                }
            }
        },
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.padding(horizontal = 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_logo_google),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            modifier = Modifier.padding(vertical = 5.dp),
            text = text,
            style = TextStyle(
                color = Color.Black
            )
        )
    }
}
