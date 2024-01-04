package com.idphoto.idphotomaster.feature.editphoto

import android.content.Context
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
import com.idphoto.idphotomaster.core.domain.usecase.home.ReadImageFromGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPhotoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val readImageFromGalleryUseCase: ReadImageFromGalleryUseCase,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) :
    BaseViewModel<EditPhotoViewState, EditPhotoViewEvent>() {
    private val photoArgs: EditPhotoArgs = EditPhotoArgs(savedStateHandle)
    override fun createInitialState(): EditPhotoViewState = EditPhotoViewState()

    init {
        readImageAndUpdateState(photoArgs.photoPath)
    }

    private fun readImageAndUpdateState(photoPath: String) {
        viewModelScope.launch(ioDispatcher) {
            readImageFromGalleryUseCase(photoPath).asResource()
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
                                    lastCapturedPhotoPath = photoArgs.photoPath,
                                    initialPhoto = converted
                                )
                            }
                            fireEvent(EditPhotoViewEvent.PhotoReadCompleted)
                        }
                    }
                }.launchIn(this)
        }
    }

    fun onBrightnessChanged(brightness: Float) {
        viewModelScope.launch(ioDispatcher) {
            uiState.value.gpuImage?.setFilter(GPUImageBrightnessFilter(brightness))
            updateState { copy(brightness = brightness, updatedPhoto = gpuImage?.bitmapWithFilterApplied) }
        }
    }

    fun onSharpnessChanged(sharpness: Float) {
        viewModelScope.launch(ioDispatcher) {
            uiState.value.gpuImage?.setFilter(GPUImageSharpenFilter(sharpness))
            updateState { copy(sharpness = sharpness, updatedPhoto = gpuImage?.bitmapWithFilterApplied) }
        }
    }

    fun onHeatChanged(heat: Float) {
        viewModelScope.launch(ioDispatcher) {
            uiState.value.gpuImage?.setFilter(GPUImageWhiteBalanceFilter(heat, 0f))
            updateState { copy(heat = heat, updatedPhoto = gpuImage?.bitmapWithFilterApplied) }
        }
    }

    fun initImage(current: Context) {
        val gpuImage = GPUImage(current)
        gpuImage.setImage(uiState.value.initialPhoto)
        updateState { copy(updatedPhoto = gpuImage.bitmapWithFilterApplied, gpuImage = gpuImage) }
    }
}

data class EditPhotoViewState(
    val loading: Boolean = false,
    val lastCapturedPhotoPath: String? = null,
    val gpuImage: GPUImage? = null,
    val updatedPhoto: Bitmap? = null,
    val initialPhoto: Bitmap? = null,
    val sharpness: Float = 1f,
    val brightness: Float = 0f,
    val heat: Float = 5000f
) : IViewState

sealed interface EditPhotoViewEvent : IViewEvents {
    data object PhotoReadCompleted : EditPhotoViewEvent
}