package com.idphoto.idphotomaster.feature.basket

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.common.dispatchers.AppDispatchers
import com.idphoto.idphotomaster.core.common.dispatchers.Dispatcher
import com.idphoto.idphotomaster.core.common.safeLet
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.usecase.basket.RollbackPurchaseUseCase
import com.idphoto.idphotomaster.core.domain.usecase.basket.StartPurchaseUseCase
import com.idphoto.idphotomaster.core.domain.usecase.home.ReadImageFromDevice
import com.idphoto.idphotomaster.core.domain.usecase.home.SavePhotoToGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import getExceptionModel
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
    private val userRepository: UserRepository,
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase,
    private val startPurchaseUseCase: StartPurchaseUseCase,
    private val rollbackPurchaseUseCase: RollbackPurchaseUseCase
) : BaseViewModel<BasketViewState>() {
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
                            updateState {
                                copy(
                                    loading = false, exception = result.exception?.getExceptionModel(
                                        descriptionResId = R.string.exception_read_image
                                    )
                                )
                            }
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
            updateState { copy(navigateToLogin = triggered) }
        } else {
            startPurchase()
        }
    }

    fun purchaseSuccess() {
        savePhotoToGallery()
    }

    private fun savePhotoToGallery() {
        viewModelScope.launch {
            uiState.value.photo?.let {
                savePhotoToGalleryUseCase(capturePhotoBitmap = it).asResource().onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            updateState {
                                copy(
                                    loading = false, exception = result.exception?.getExceptionModel(
                                        descriptionResId = R.string.exception_save_image_to_gallery
                                    )
                                )
                            }
                        }

                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Success -> {
                            updateState { copy(loading = false, purchaseCompleted = triggered) }
                        }
                    }
                }.launchIn(this)
            }
        }
    }

    fun rollbackPurchase() {
        viewModelScope.launch {
            safeLet(userRepository.currentUser?.uid, uiState.value.lastPurchasedPhotoId) { uid, photoId ->
                rollbackPurchaseUseCase.invoke(uid, photoId).asResource().onEach {
                    when (it) {
                        is Resource.Error -> updateState { copy(lastPurchasedPhotoId = null) }
                        Resource.Loading -> {}
                        is Resource.Success -> updateState { copy(lastPurchasedPhotoId = null) }
                    }
                }.launchIn(this)
            }
        }
    }

    private fun startPurchase() {
        viewModelScope.launch {
            safeLet(userRepository.currentUser?.uid, uiState.value.photo) { uid, photo ->
                startPurchaseUseCase(uid, photo).asResource().onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            updateState {
                                copy(
                                    loading = false, exception = result.exception?.getExceptionModel(
                                        descriptionResId = R.string.exception_start_purchase,
                                        primaryButtonTextResId = null
                                    )
                                )
                            }
                        }

                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Success -> {
                            updateState {
                                copy(
                                    loading = false, startGooglePurchase = triggered,
                                    lastPurchasedPhotoId = result.data
                                )
                            }
                        }
                    }
                }.launchIn(this)
            }
        }
    }

    fun onNavigateToLoginConsumed() {
        updateState { copy(navigateToLogin = consumed) }
    }

    fun onStartGooglePurchaseConsumed() {
        updateState { copy(startGooglePurchase = consumed) }
    }

    fun onPurchaseCompletedConsumed() {
        updateState { copy(purchaseCompleted = consumed) }
    }

    fun onErrorDialogDismiss() {
        updateState { copy(exception = null) }
    }
}

data class BasketViewState(
    val loading: Boolean = false,
    val photo: Bitmap? = null,
    val navigateToLogin: StateEvent = consumed,
    val startGooglePurchase: StateEvent = consumed,
    val purchaseCompleted: StateEvent = consumed,
    val exception: ExceptionModel? = null,
    val lastPurchasedPhotoId: String? = null
) : IViewState