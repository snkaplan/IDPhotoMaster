package com.idphoto.idphotomaster.feature.login.contents

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.LightGrey
import com.idphoto.idphotomaster.feature.login.LoginViewEvent
import com.idphoto.idphotomaster.feature.login.LoginViewState
import com.idphoto.idphotomaster.feature.login.components.PasswordTextField
import com.idphoto.idphotomaster.feature.login.components.UserInputTextField
import com.idphoto.idphotomaster.feature.profile.EnterEmailBottomSheet
import de.palm.composestateevents.EventEffect

@Composable
fun LoginScreenContent(
    viewState: LoginViewState,
    onViewEvent: (LoginViewEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val showForgotPasswordBottomSheet = remember { mutableStateOf(false) }
    EventEffect(
        event = viewState.passwordResetMailSent,
        onConsumed = {
            onViewEvent.invoke(LoginViewEvent.OnResetPasswordMailConsumed)
        },
        action = {
            Toast.makeText(context, context.getString(R.string.mail_error), Toast.LENGTH_LONG).show()
        }
    )
    if (showForgotPasswordBottomSheet.value) {
        EnterEmailBottomSheet(
            modifier = Modifier,
            onDismissRequest = { showForgotPasswordBottomSheet.value = false },
            onConfirm = {
                showForgotPasswordBottomSheet.value = false
                onViewEvent.invoke(LoginViewEvent.OnSendResetPasswordMail(it))
            }
        )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            modifier = Modifier
                .width(280.dp)
                .height(250.dp),
            painter = painterResource(id = R.drawable.ic_login),
            contentDescription = "Login Icon"
        )
        Spacer(modifier = Modifier.height(20.dp))
        UserInputTextField(
            value = viewState.mail,
            modifier = Modifier.padding(horizontal = 16.dp),
            errorMessageRes = viewState.mailErrorMessage,
            onValueChange = { onViewEvent.invoke(LoginViewEvent.OnChangeMail(it)) },
            placeholder = R.string.mail_hint,
            keyboardActions = KeyboardActions {
                focusManager.moveFocus(FocusDirection.Down)
            },
            enabled = viewState.loading.not()
        )
        PasswordTextField(
            value = viewState.password,
            errorMessageRes = viewState.passwordErrorMessage,
            onValueChange = { onViewEvent.invoke(LoginViewEvent.OnPasswordChange(it)) },
            placeholder = R.string.password_hint,
            enabled = viewState.loading.not(),
            keyboardActions = KeyboardActions { keyboardController?.hide() }
        )
        Text(
            text = stringResource(id = R.string.forgot_password),
            style = TextStyle(color = LightGrey, fontSize = 12.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .clickable {
                    showForgotPasswordBottomSheet.value = true
                },
            textAlign = TextAlign.Start,
        )
    }
}