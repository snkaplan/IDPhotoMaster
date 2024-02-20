package com.idphoto.idphotomaster.feature.splash

import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.data.datasource.local.LocalDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val localDataStore: LocalDataStore,
) : BaseViewModel<SplashViewState>() {
    override fun createInitialState(): SplashViewState = SplashViewState()

    init {
        viewModelScope.launch {
            localDataStore.isUserSawTutorial().collectLatest {
                updateState { copy(isUserSawTutorial = it) }
                delay(1000)
                if (currentState.isUserSawTutorial) {
                    updateState { copy(navigateToHome = triggered) }
                } else {
                    updateState { copy(navigateToTutorial = triggered) }
                }
                return@collectLatest
            }
        }
    }

    fun onTriggerViewEvent(event: SplashViewEvent) {
        when (event) {
            SplashViewEvent.OnNavigateToHomeConsumed -> onNavigateToHomeConsumed()
            SplashViewEvent.OnNavigateToTutorialConsumed -> onNavigateToTutorialConsumed()
        }
    }

    private fun onNavigateToHomeConsumed() {
        updateState { copy(navigateToHome = consumed) }
    }

    private fun onNavigateToTutorialConsumed() {
        updateState { copy(navigateToTutorial = consumed) }
    }
}

data class SplashViewState(
    val loading: Boolean = false,
    val isUserSawTutorial: Boolean = false,
    val navigateToHome: StateEvent = consumed,
    val navigateToTutorial: StateEvent = consumed
) : IViewState

sealed interface SplashViewEvent {
    data object OnNavigateToHomeConsumed : SplashViewEvent
    data object OnNavigateToTutorialConsumed : SplashViewEvent
}