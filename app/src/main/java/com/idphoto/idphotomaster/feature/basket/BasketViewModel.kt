package com.idphoto.idphotomaster.feature.basket

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.common.dispatchers.AppDispatchers
import com.idphoto.idphotomaster.core.common.dispatchers.Dispatcher
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.usecase.home.ReadImageFromDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val readImageFromDevice: ReadImageFromDevice,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepository
) :
    BaseViewModel<BasketViewState, BasketViewEvent>() {
    private val photoArgs: BasketArgs = BasketArgs(savedStateHandle)
    override fun createInitialState(): BasketViewState = BasketViewState()

    init {
        readImageAndUpdateState(photoArgs.selectedPhotoPath)
    }

    private fun readImageAndUpdateState(photoPath: String) {
        viewModelScope.launch(ioDispatcher) {
            readImageFromDevice(photoPath).asResource()
                .onEach { result ->
                    when (result) {
                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Error -> {
                            updateState { copy(loading = false) }
                        }

                        is Resource.Success -> {
                            val converted: Bitmap = result.data.copy(Bitmap.Config.ARGB_8888, false)
                            updateState {
                                copy(
                                    photo = converted,
                                    loading = false
                                )
                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    fun onCompletePurchase() {
        if (userRepository.currentUser == null) {
            fireEvent(BasketViewEvent.NavigateToLogin)
        } else {
            fireEvent(BasketViewEvent.StartGooglePurchase)
        }
    }
}

data class BasketViewState(
    val loading: Boolean = false,
    val photo: Bitmap? = null
) : IViewState

sealed interface BasketViewEvent : IViewEvents {
    data object NavigateToLogin : BasketViewEvent
    data object StartGooglePurchase : BasketViewEvent
}