package com.idphoto.idphotomaster.feature.savedphotos

import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.UserSavedPhoto
import com.idphoto.idphotomaster.core.domain.usecase.profile.GetUserPurchases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedPhotosViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserPurchases: GetUserPurchases
) :
    BaseViewModel<SavedPhotosViewState, SavedPhotoViewEvents>() {
    override fun createInitialState(): SavedPhotosViewState = SavedPhotosViewState()

    fun init() {
        userRepository.currentUser?.let {
            getUserPurchases(it.uid)
        }
    }

    private fun getUserPurchases(uid: String) {
        viewModelScope.launch {
            getUserPurchases.invoke(uid).asResource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        updateState { copy(loading = false) }
                    }

                    is Resource.Success -> {
                        updateState { copy(loading = false, savedPhotos = result.data) }
                    }
                }
            }.launchIn(this)
        }
    }
}

data class SavedPhotosViewState(val loading: Boolean = false, val savedPhotos: List<UserSavedPhoto>? = null) :
    IViewState

sealed class SavedPhotoViewEvents : IViewEvents {}