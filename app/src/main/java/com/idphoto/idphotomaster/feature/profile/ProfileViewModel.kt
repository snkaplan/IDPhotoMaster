package com.idphoto.idphotomaster.feature.profile

import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.InfoBottomSheetItem
import com.idphoto.idphotomaster.core.domain.model.User
import com.idphoto.idphotomaster.core.domain.usecase.profile.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserUseCase: GetUserUseCase
) :
    BaseViewModel<ProfileViewState, ProfileViewEvents>() {
    override fun createInitialState(): ProfileViewState = ProfileViewState()

    fun init() {
        userRepository.currentUser?.let {
            getUser(it.uid)
        }
    }

    private fun getUser(uid: String) {
        viewModelScope.launch {
            getUserUseCase.invoke(uid).asResource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        updateState { copy(loading = false) }
                    }

                    is Resource.Success -> {
                        updateState { copy(loading = false, user = result.data, loggedIn = true) }
                    }
                }
            }.launchIn(this)
        }
    }

    fun onTriggerEvent(event: ProfileViewTriggeredEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfileViewTriggeredEvent.ShowInfoBottomSheet -> {
                    updateState { copy(infoBottomSheet = InfoBottomSheetItem(event.title, event.description)) }
                }

                ProfileViewTriggeredEvent.InfoBottomSheetDismissed -> {
                    updateState { copy(infoBottomSheet = null) }
                }
            }
        }
    }
}

data class ProfileViewState(
    val loading: Boolean = false,
    val loggedIn: Boolean = false,
    val user: User? = null,
    val infoBottomSheet: InfoBottomSheetItem? = null
) : IViewState

sealed class ProfileViewEvents : IViewEvents {}

sealed class ProfileViewTriggeredEvent {
    data class ShowInfoBottomSheet(val title: String, val description: String) : ProfileViewTriggeredEvent()
    data object InfoBottomSheetDismissed : ProfileViewTriggeredEvent()
}