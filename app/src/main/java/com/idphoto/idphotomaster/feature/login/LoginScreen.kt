package com.idphoto.idphotomaster.feature.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.CustomTextField
import com.idphoto.idphotomaster.core.systemdesign.ui.LocalDim
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.White

@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = viewModel.uiEvents) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                LoginViewEvents.NavigateToHome -> navigateToHome.invoke()
            }
        }
    }
    ScreenContent(
        viewState = viewState,
        modifier = modifier.fillMaxSize(),
        onMailValueChange = viewModel::onMailChange,
        onPasswordValueChange = viewModel::onPasswordChange,
    )
}

@Composable
fun ScreenContent(
    viewState: LoginViewState,
    onMailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {},
    ) { padding ->
        Column(
            modifier = modifier
                .background(BackgroundColor)
                .padding(padding)
                .padding(LocalDim.current.pageMargin)
                .fillMaxSize()
                .imePadding()
        ) {
            LoginContent(viewState, onMailValueChange, onPasswordValueChange)
        }
    }
}

@Composable
fun LoginContent(
    viewState: LoginViewState,
    onMailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Hoşgeldin",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Şipşak fotoğrafın vücut bulmuş hali :D",
            modifier = Modifier
                .fillMaxWidth(),
            style = TextStyle(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            modifier = Modifier
                .width(300.dp)
                .height(250.dp),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.ic_login),
            contentDescription = "Login Icon"
        )
        Spacer(modifier = Modifier.height(20.dp))
        MailTextField(
            value = viewState.mail,
            errorMessageRes = viewState.mailErrorMessage,
            onValueChange = onMailValueChange
        )
        PasswordTextField(
            value = viewState.password,
            errorMessageRes = viewState.passwordErrorMessage,
            onValueChange = onPasswordValueChange,
        )
    }
}

@Composable
private fun MailTextField(
    value: String,
    errorMessageRes: Int?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = value,
        label = { Text(text = stringResource(id = R.string.mail_hint)) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White
        ),
        onValueChange = onValueChange,
        isError = errorMessageRes != null,
        trailingIcon = {
            AnimatedVisibility(visible = value.isNotEmpty()) {
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        placeholder = { Text(text = stringResource(id = R.string.mail_hint)) },
    )
}

@Composable
private fun PasswordTextField(
    value: String,
    errorMessageRes: Int?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    CustomTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = value,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White
        ),
        label = { Text(text = stringResource(id = R.string.password_hint)) },
        onValueChange = onValueChange,
        isError = errorMessageRes != null,
        trailingIcon = {
            AnimatedVisibility(visible = value.isNotEmpty()) {
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        placeholder = { Text(text = stringResource(id = R.string.password_hint)) },
    )
}