package com.idphoto.idphotomaster.feature.login.contents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.feature.login.LoginViewState
import com.idphoto.idphotomaster.feature.login.components.PasswordTextField
import com.idphoto.idphotomaster.feature.login.components.UserInputTextField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignupScreenContent(
    viewState: LoginViewState,
    onMailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onNameValeChange: (String) -> Unit,
    onLastnameValueChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val downAction = KeyboardActions {
        focusManager.moveFocus(FocusDirection.Down)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        UserInputTextField(
            value = viewState.name,
            errorMessageRes = viewState.nameErrorMessage,
            modifier = Modifier.padding(horizontal = 16.dp),
            onValueChange = onNameValeChange,
            placeholder = R.string.name,
            enabled = viewState.loading.not(),
            keyboardActions = downAction
        )
        UserInputTextField(
            value = viewState.lastName,
            errorMessageRes = viewState.lastNameErrorMessage,
            modifier = Modifier.padding(horizontal = 16.dp),
            onValueChange = onLastnameValueChange,
            placeholder = R.string.surname,
            enabled = viewState.loading.not(),
            keyboardActions = downAction
        )
        UserInputTextField(
            value = viewState.mail,
            errorMessageRes = viewState.mailErrorMessage,
            modifier = Modifier.padding(horizontal = 16.dp),
            onValueChange = onMailValueChange,
            placeholder = R.string.mail_hint,
            enabled = viewState.loading.not(),
            keyboardActions = downAction
        )
        PasswordTextField(
            value = viewState.password,
            errorMessageRes = viewState.passwordErrorMessage,
            onValueChange = onPasswordValueChange,
            placeholder = R.string.password_hint,
            enabled = viewState.loading.not(),
            keyboardActions = KeyboardActions { keyboardController?.hide() }
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}