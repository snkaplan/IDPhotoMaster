package com.idphoto.idphotomaster.feature.splash

import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.data.datasource.local.LocalDataStore
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localDataStore: LocalDataStore,
) : BaseViewModel<SplashViewState, SplashViewEvents>() {
    override fun createInitialState(): SplashViewState = SplashViewState()

    init {
        viewModelScope.launch {
            localDataStore.isUserSawTutorial().collectLatest {
                updateState { copy(isUserSawTutorial = it) }
                delay(1000)
                if (currentState.isUserSawTutorial) {
                    fireEvent(SplashViewEvents.NavigateToHome)
                } else {
                    fireEvent(SplashViewEvents.NavigateToTutorial)
                }
                return@collectLatest
            }
        }
    }
}

data class SplashViewState(val loading: Boolean = false, val isUserSawTutorial: Boolean = false) :
    IViewState

sealed class SplashViewEvents : IViewEvents {
    data object NavigateToHome : SplashViewEvents()
    data object NavigateToTutorial : SplashViewEvents()
}