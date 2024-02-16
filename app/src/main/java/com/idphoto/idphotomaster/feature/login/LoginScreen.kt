package com.idphoto.idphotomaster.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.components.AppScaffold
import com.idphoto.idphotomaster.core.systemdesign.components.ErrorDialog
import com.idphoto.idphotomaster.core.systemdesign.ui.LocalDim
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.LightGrey
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.White
import com.idphoto.idphotomaster.feature.login.components.GoogleSignInButton
import com.idphoto.idphotomaster.feature.login.contents.LoginScreenContent
import com.idphoto.idphotomaster.feature.login.contents.SignupScreenContent
import de.palm.composestateevents.NavigationEventEffect

@Composable
fun LoginScreen(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val viewState by viewModel.uiState.collectAsStateWithLifecycle()
    NavigationEventEffect(
        event = viewState.loginSuccessful,
        onConsumed = viewModel::onLoginSuccessfulConsumed,
        action = onCloseClick
    )

    ErrorDialog(
        exception = viewState.exception,
        onDismissRequest = {
            viewModel.onErrorDialogDismiss()
        },
        onButtonClick = {
            viewModel.onErrorDialogDismiss()
        },
    )
    ScreenContent(
        viewState = viewState,
        modifier = modifier.fillMaxSize(),
        onMailValueChange = viewModel::onMailChange,
        onPasswordValueChange = viewModel::onPasswordChange,
        onStateChange = viewModel::onPageStateChange,
        onNameValueChange = viewModel::onNameChange,
        onLastnameValueChange = viewModel::onLastnameChange,
        onAction = if (viewState.pageState == PageState.LOGIN) viewModel::onLoginClick else viewModel::onSignupClick,
        onCloseClick = onCloseClick,
        onGoogleSignInClicked = viewModel::loginWithGoogle
    )
}

@Composable
fun ScreenContent(
    viewState: LoginViewState,
    onMailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onStateChange: (PageState) -> Unit,
    onNameValueChange: (String) -> Unit,
    onLastnameValueChange: (String) -> Unit,
    onAction: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    onGoogleSignInClicked: (String) -> Unit
) {
    val titleTextId = remember { mutableIntStateOf(-1) }
    val descriptionTextId = remember { mutableIntStateOf(-1) }
    val infoFirstTextId = remember { mutableIntStateOf(-1) }
    val infoSecondTextId = remember { mutableIntStateOf(-1) }
    val buttonTextId = remember(viewState.pageState) {
        mutableIntStateOf(
            when (viewState.pageState) {
                PageState.LOGIN -> {
                    titleTextId.intValue = R.string.login_title
                    descriptionTextId.intValue = R.string.login_description
                    infoFirstTextId.intValue = R.string.dont_have_an_account
                    infoSecondTextId.intValue = R.string.signup
                    R.string.login
                }

                PageState.SIGNUP -> {
                    titleTextId.intValue = R.string.signup_title
                    descriptionTextId.intValue = R.string.signup_description
                    infoFirstTextId.intValue = R.string.have_an_account
                    infoSecondTextId.intValue = R.string.login
                    R.string.signup
                }
            }
        )
    }
    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {},
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .background(BackgroundColor)
                .padding(padding)
                .padding(LocalDim.current.pageMargin)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Blue)
                        .clickable { onCloseClick() }
                        .padding(5.dp),
                    imageVector = Icons.Filled.Close,
                    contentDescription = "",
                    tint = Color.White
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "Star",
                    modifier = Modifier.size(40.dp)
                )
            }

            if (viewState.loading) {
                LinearProgressIndicator(color = Blue)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = titleTextId.intValue),
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(horizontal = 40.dp),
                text = stringResource(descriptionTextId.intValue),
                style = TextStyle(fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Center
            )
            if (viewState.pageState == PageState.LOGIN) {
                LoginScreenContent(
                    viewState,
                    onMailValueChange,
                    onPasswordValueChange
                )
            } else {
                SignupScreenContent(
                    viewState,
                    onMailValueChange,
                    onPasswordValueChange,
                    onNameValueChange,
                    onLastnameValueChange
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                onClick = { onAction.invoke() }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(10.dp),
                enabled = viewState.loading.not()
            ) {
                Text(
                    text = stringResource(buttonTextId.intValue),
                    modifier = Modifier.padding(12.dp),
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = White)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            ClickableText(style = TextStyle(textAlign = TextAlign.Center), text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = LightGrey,
                        fontSize = 10.sp
                    )
                ) {
                    append(stringResource(id = infoFirstTextId.intValue) + " ")
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(stringResource(id = infoSecondTextId.intValue))
                }
                withStyle(
                    style = SpanStyle(
                        color = LightGrey,
                        fontSize = 10.sp,
                    )
                ) {
                    append("\n \n" + stringResource(id = R.string.or))
                }
            }) {
                if (viewState.loading.not()) {
                    onStateChange(if (viewState.pageState == PageState.LOGIN) PageState.SIGNUP else PageState.LOGIN)
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            GoogleSignInButton(
                text = stringResource(id = R.string.sign_in_with_google),
                modifier = Modifier.fillMaxWidth(),
                onSuccess = { idToken ->
                    onGoogleSignInClicked.invoke(idToken)
                },
                onError = { errorMessage ->
                    // The user failed to sign in with Google
                }
            )
        }
    }
}