package com.idphoto.idphotomaster.feature.login

import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.domain.exceptions.LastNameRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.MailRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.NameRequiredException
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordLengthException
import com.idphoto.idphotomaster.core.domain.exceptions.PasswordRequiredException
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.usecase.login.ForgotPasswordUseCase
import com.idphoto.idphotomaster.core.domain.usecase.login.GoogleLoginUseCase
import com.idphoto.idphotomaster.core.domain.usecase.login.LoginUseCase
import com.idphoto.idphotomaster.core.domain.usecase.login.SignupUseCase
import com.idphoto.idphotomaster.core.domain.usecase.login.ValidateAuthUseCase
import com.idphoto.idphotomaster.core.domain.usecase.login.ValidateSignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import getExceptionModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val signupUseCase: SignupUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val validateAuthUseCase: ValidateAuthUseCase,
    private val validateSignupUseCase: ValidateSignupUseCase
) : BaseViewModel<LoginViewState>() {

    override fun createInitialState(): LoginViewState = LoginViewState()

    fun onTriggerViewEvent(viewEvent: LoginViewEvent) {
        viewModelScope.launch {
            when (viewEvent) {
                LoginViewEvent.OnLoginClick -> onLoginClick()
                LoginViewEvent.OnSignupClick -> onSignupClick()
                is LoginViewEvent.OnLoginWithGoogle -> loginWithGoogle(viewEvent.token)
                is LoginViewEvent.OnChangeLastname -> onLastnameChange(viewEvent.lastname)
                is LoginViewEvent.OnChangeMail -> onMailChange(viewEvent.mail)
                is LoginViewEvent.OnChangeName -> onNameChange(viewEvent.name)
                is LoginViewEvent.OnPasswordChange -> onPasswordChange(viewEvent.password)
                is LoginViewEvent.OnPageStateChange -> onPageStateChange(viewEvent.state)
                is LoginViewEvent.OnSendResetPasswordMail -> onSendResetPasswordMail(viewEvent.mail)
                LoginViewEvent.OnDismissErrorDialog -> updateState { copy(exception = null) }
                LoginViewEvent.OnLoginSuccessfulConsumed -> updateState { copy(loginSuccessful = consumed) }
                LoginViewEvent.OnResetPasswordMailConsumed -> updateState { copy(passwordResetMailSent = consumed) }
                LoginViewEvent.OnCloseClick -> updateState { copy(closeClicked = triggered) }
                LoginViewEvent.OnCloseClickConsumed -> updateState { copy(closeClicked = consumed) }
            }
        }
    }

    private suspend fun onLoginClick() {
        withContext(currentCoroutineContext()) {
            validateAuthUseCase.invoke(currentState.mail, currentState.password)
                .onEach { result ->
                    when (result) {
                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Error -> {
                            result.exception?.let { handleError(it) } ?: run {
                                updateState { copy(loading = false) }
                            }
                        }

                        is Resource.Success -> {
                            updateState { copy(loading = false) }
                            onLogin(currentState.mail, currentState.password)
                        }
                    }
                }.launchIn(this)
        }
    }

    private fun onLogin(mail: String, password: String) {
        viewModelScope.launch {
            loginUseCase(mail, password)
                .asResource()
                .onEach { result ->
                    when (result) {
                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Error -> {
                            updateState {
                                copy(
                                    loading = false, exception = result.exception?.getExceptionModel(
                                        descriptionResId = R.string.exception_login,
                                        primaryButtonTextResId = null
                                    )
                                )
                            }
                        }

                        is Resource.Success -> {
                            updateState {
                                copy(loading = false, loginSuccessful = triggered)
                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    private suspend fun onSignupClick() {
        withContext(currentCoroutineContext()) {
            validateSignupUseCase.invoke(
                currentState.name,
                currentState.lastName,
                currentState.mail,
                currentState.password
            ).onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        result.exception?.let { handleError(it) } ?: run {
                            updateState { copy(loading = false) }
                        }
                    }

                    is Resource.Success -> {
                        updateState { copy(loading = false) }
                        onSignup()
                    }
                }
            }.launchIn(this)
        }
    }

    private fun onSignup() {
        viewModelScope.launch {
            signupUseCase(
                currentState.name,
                currentState.lastName,
                currentState.mail,
                currentState.password
            )
                .asResource()
                .onEach { result ->
                    when (result) {
                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Error -> {
                            updateState {
                                copy(
                                    loading = false, exception = result.exception?.getExceptionModel(
                                        descriptionResId = R.string.exception_signup,
                                        primaryButtonTextResId = null
                                    )
                                )
                            }
                        }

                        is Resource.Success -> {
                            updateState {
                                copy(loading = false, loginSuccessful = triggered)
                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    private suspend fun loginWithGoogle(idToken: String) {
        withContext(currentCoroutineContext()) {
            googleLoginUseCase.invoke(idToken).asResource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        updateState {
                            copy(
                                loading = false, exception = result.exception?.getExceptionModel(
                                    descriptionResId = R.string.exception_login_google,
                                    primaryButtonTextResId = null
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        updateState {
                            copy(loading = false, loginSuccessful = triggered)
                        }
                    }
                }
            }.launchIn(this)
        }
    }

    private fun onMailChange(mail: String) {
        updateState { copy(mail = mail, mailErrorMessage = null) }
    }

    private fun onPasswordChange(password: String) {
        updateState { copy(password = password, passwordErrorMessage = null) }
    }

    private fun onNameChange(name: String) {
        updateState { copy(name = name, nameErrorMessage = null) }
    }

    private fun onLastnameChange(lastName: String) {
        updateState { copy(lastName = lastName, lastNameErrorMessage = null) }
    }

    private fun onPageStateChange(newPageState: PageState) {
        updateState { LoginViewState(pageState = newPageState) }
    }

    private suspend fun onSendResetPasswordMail(mail: String) {
        withContext(currentCoroutineContext()) {
            forgotPasswordUseCase.invoke(mail).asResource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        updateState {
                            copy(
                                loading = false, exception = result.exception?.getExceptionModel(
                                    descriptionResId = R.string.exception_send_password_reset_email,
                                    primaryButtonTextResId = null
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        updateState {
                            copy(loading = false, passwordResetMailSent = triggered)
                        }
                    }
                }
            }.launchIn(this)
        }
    }

    private fun handleError(th: Throwable) {
        when (th) {
            is NameRequiredException -> {
                updateState { copy(loading = false, nameErrorMessage = R.string.name_empty_error) }
            }

            is LastNameRequiredException -> {
                updateState {
                    copy(
                        loading = false,
                        lastNameErrorMessage = R.string.lastname_empty_error
                    )
                }
            }

            is MailRequiredException -> {
                updateState { copy(loading = false, mailErrorMessage = R.string.mail_error) }
            }

            is PasswordRequiredException -> {
                updateState {
                    copy(
                        loading = false,
                        passwordErrorMessage = R.string.password_error
                    )
                }
            }

            is PasswordLengthException -> {
                updateState {
                    copy(
                        loading = false,
                        passwordErrorMessage = R.string.password_length_error
                    )
                }
            }

            else -> {
                updateState {
                    copy(
                        loading = false,
                        exception = th.getExceptionModel()
                    )
                }
            }
        }
    }
}

data class LoginViewState(
    val loading: Boolean = false,
    val mail: String = "",
    val name: String = "",
    val lastName: String = "",
    val password: String = "",
    val passwordErrorMessage: Int? = null,
    val mailErrorMessage: Int? = null,
    val nameErrorMessage: Int? = null,
    val lastNameErrorMessage: Int? = null,
    val pageState: PageState = PageState.LOGIN,
    val loginSuccessful: StateEvent = consumed,
    val passwordResetMailSent: StateEvent = consumed,
    val closeClicked: StateEvent = consumed,
    val exception: ExceptionModel? = null
) : IViewState

enum class PageState {
    LOGIN,
    SIGNUP
}

sealed interface LoginViewEvent {
    data object OnLoginClick : LoginViewEvent
    data object OnSignupClick : LoginViewEvent
    data class OnLoginWithGoogle(val token: String) : LoginViewEvent
    data class OnChangeMail(val mail: String) : LoginViewEvent
    data class OnChangeName(val name: String) : LoginViewEvent
    data class OnChangeLastname(val lastname: String) : LoginViewEvent
    data class OnPasswordChange(val password: String) : LoginViewEvent
    data class OnPageStateChange(val state: PageState) : LoginViewEvent
    data class OnSendResetPasswordMail(val mail: String) : LoginViewEvent
    data object OnLoginSuccessfulConsumed : LoginViewEvent
    data object OnDismissErrorDialog : LoginViewEvent
    data object OnResetPasswordMailConsumed : LoginViewEvent
    data object OnCloseClick : LoginViewEvent
    data object OnCloseClickConsumed : LoginViewEvent
}