package com.idphoto.idphotomaster.feature.login

import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.domain.login.LoginUseCase
import com.idphoto.idphotomaster.core.domain.login.ValidateAuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateAuthUseCase: ValidateAuthUseCase,
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

    private fun onLogin(username: String, password: String) {
        viewModelScope.launch {
            loginUseCase(username, password)
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
}

data class LoginViewState(
    val loading: Boolean = false,
    val mail: String = "",
    val password: String = "",
    val passwordErrorMessage: Int? = null,
    val mailErrorMessage: Int? = null
) : IViewState


sealed class LoginViewEvents : IViewEvents {
    data object NavigateToHome : LoginViewEvents()
}