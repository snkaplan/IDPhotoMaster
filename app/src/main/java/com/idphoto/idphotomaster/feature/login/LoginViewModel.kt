package com.idphoto.idphotomaster.feature.login

import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.domain.usecase.login.LoginUseCase
import com.idphoto.idphotomaster.core.domain.usecase.login.SignupUseCase
import com.idphoto.idphotomaster.core.domain.usecase.login.ValidateAuthUseCase
import com.idphoto.idphotomaster.core.domain.usecase.login.ValidateSignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase,
    private val validateAuthUseCase: ValidateAuthUseCase,
    private val validateSignupUseCase: ValidateSignupUseCase
) : BaseViewModel<LoginViewState, LoginViewEvents>() {

    override fun createInitialState(): LoginViewState = LoginViewState()

    fun onLoginClick() {
        viewModelScope.launch {
            validateAuthUseCase.invoke(currentState.mail, currentState.password)
                .onEach { result ->
                    when (result) {
                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Error -> {
                            updateState { copy(loading = false) }
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
                            updateState { copy(loading = false) }
                        }

                        is Resource.Success -> {
                            updateState {
                                copy(loading = false)
                            }
                            fireEvent(LoginViewEvents.NavigateToHome)
                        }
                    }
                }.launchIn(this)
        }
    }

    fun onSignupClick() {
        viewModelScope.launch {
            validateSignupUseCase.invoke(
                currentState.name,
                currentState.lastName,
                currentState.mail,
                currentState.password,
                currentState.passwordAgain
            ).onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        updateState { copy(loading = false) }
                    }

                    is Resource.Success -> {
                        updateState { copy(loading = false) }
                        onLogin(currentState.mail, currentState.password)
                    }
                }
            }.launchIn(this)
        }
    }

    fun onSignup(name: String, lastName: String, mail: String, password: String) {
        viewModelScope.launch {
            signupUseCase(name, lastName, mail, password)
                .asResource()
                .onEach { result ->
                    when (result) {
                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Error -> {
                            updateState { copy(loading = false) }
                        }

                        is Resource.Success -> {
                            updateState {
                                copy(loading = false)
                            }
                            fireEvent(LoginViewEvents.NavigateToHome)
                        }
                    }
                }.launchIn(this)
        }
    }

    fun onMailChange(mail: String) {
        updateState { copy(mail = mail, mailErrorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        updateState { copy(password = password, passwordErrorMessage = null) }
    }

    fun onNameChange(name: String) {
        updateState { copy(name = name, nameErrorMessage = null) }
    }

    fun onLastnameChange(lastName: String) {
        updateState { copy(lastName = lastName, lastNameErrorMessage = null) }
    }

    fun onPasswordAgainChange(password: String) {
        updateState { copy(passwordAgain = password, passwordErrorMessage = null) }
    }

    fun onPageStateChange(newPageState: PageState) {
        updateState { LoginViewState(pageState = newPageState) }
    }
}

data class LoginViewState(
    val loading: Boolean = false,
    val mail: String = "",
    val name: String = "",
    val lastName: String = "",
    val password: String = "",
    val passwordAgain: String = "",
    val passwordErrorMessage: Int? = null,
    val mailErrorMessage: Int? = null,
    val nameErrorMessage: Int? = null,
    val lastNameErrorMessage: Int? = null,
    val pageState: PageState = PageState.LOGIN
) : IViewState

sealed class LoginViewEvents : IViewEvents {
    data object NavigateToHome : LoginViewEvents()
}

enum class PageState {
    LOGIN,
    SIGNUP
}