package com.idphoto.idphotomaster.feature.tutorial

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.data.datasource.local.LocalDataStore
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorialViewModel @Inject constructor(
    private val localDataStore: LocalDataStore,
    private val userRepository: UserRepository
) : BaseViewModel<TutorialViewState, TutorialViewEvents>() {
    override fun createInitialState(): TutorialViewState = TutorialViewState()

    init {
        viewModelScope.launch {
            delay(1000)
            //checkUserStatusAndNavigate()
        }
    }

    fun onSkipClicked() {
        checkUserStatusAndNavigate()
    }

    private fun checkUserStatusAndNavigate() {
        if (userRepository.currentUser != null) {
            fireEvent(TutorialViewEvents.NavigateToHome)
        } else {
            fireEvent(TutorialViewEvents.NavigateToLogin)
        }
    }
}

data class TutorialViewState(
    val loading: Boolean = false,
    val tutorialItems: List<TutorialPageItem> = listOf(
        TutorialPageItem(
            R.drawable.ic_tutorial_biometric,
            R.string.tutorial_biometric_title,
            R.string.tutorial_biometric_description
        ),
        TutorialPageItem(
            R.drawable.ic_tutorial_edit,
            R.string.tutorial_edit_bg_title,
            R.string.tutorial_edit_bg_description
        )
    )
) : IViewState

sealed class TutorialViewEvents : IViewEvents {
    data object NavigateToLogin : TutorialViewEvents()
    data object NavigateToHome : TutorialViewEvents()
}

data class TutorialPageItem(
    @DrawableRes val imageId: Int,
    @StringRes val titleId: Int,
    @StringRes val descriptionId: Int
)