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
import com.idphoto.idphotomaster.core.common.safeLet
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.usecase.basket.PurchaseSuccessUseCase
import com.idphoto.idphotomaster.core.domain.usecase.home.ReadImageFromDevice
import com.idphoto.idphotomaster.core.domain.usecase.home.SavePhotoToGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val readImageFromDevice: ReadImageFromDevice,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val userRepository: UserRepository,
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase,
    private val purchaseSuccessUseCase: PurchaseSuccessUseCase
) : BaseViewModel<BasketViewState, BasketViewEvent>() {
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

    fun purchaseSuccess() {
        viewModelScope.launch {
            updateState { copy(loading = true) }
            val savePhotoToGallery = async { savePhotoToGallery() }
            val purchaseComplete = async { purchaseComplete() }
            awaitAll(savePhotoToGallery, purchaseComplete)
            fireEvent(BasketViewEvent.PurchaseCompleted)
            updateState { copy(loading = false) }
        }
    }

    private suspend fun savePhotoToGallery() {
        uiState.value.photo?.let {
            savePhotoToGalleryUseCase(capturePhotoBitmap = it).asResource().launchIn(viewModelScope).join()
        }
    }

    private suspend fun purchaseComplete() {
        uiState.value.photo?.let {
            safeLet(userRepository.currentUser?.uid, uiState.value.photo) { uid, photo ->
                purchaseSuccessUseCase(
                    uid, UUID.randomUUID().toString(), photo
                ).asResource().launchIn(viewModelScope).join()
            }
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
    data object PurchaseCompleted : BasketViewEvent
}