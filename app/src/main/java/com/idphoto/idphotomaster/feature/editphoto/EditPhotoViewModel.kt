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
import com.slowmac.autobackgroundremover.BackgroundRemover
import com.slowmac.autobackgroundremover.OnBackgroundChangeListener
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
                                    initialPhoto = converted,
                                    loading = false
                                )
                            }
                            fireEvent(EditPhotoViewEvent.PhotoReadCompleted)
                        }
                    }
                }.launchIn(this)
        }
    }

    fun onBrightnessChanged(brightness: Float) {
        uiState.value.gpuImage?.let { safeImage ->
            viewModelScope.launch(ioDispatcher) {
                safeImage.setFilter(GPUImageBrightnessFilter(brightness))
                val bitmap = safeImage.bitmapWithFilterApplied
                updateState {
                    copy(
                        brightness = brightness,
                        updatedPhoto = bitmap,
                        lastUpdatedPhotoWithBackground = bitmap
                    )
                }
            }
        }
    }

    fun onSharpnessChanged(sharpness: Float) {
        uiState.value.gpuImage?.let { safeImage ->
            viewModelScope.launch(ioDispatcher) {
                safeImage.setFilter(GPUImageSharpenFilter(sharpness))
                val bitmap = safeImage.bitmapWithFilterApplied
                updateState {
                    copy(
                        sharpness = sharpness,
                        updatedPhoto = bitmap,
                        lastUpdatedPhotoWithBackground = bitmap
                    )
                }
            }
        }
    }

    fun onHeatChanged(heat: Float) {
        uiState.value.gpuImage?.let { safeImage ->
            viewModelScope.launch(ioDispatcher) {
                safeImage.setFilter(GPUImageWhiteBalanceFilter(heat, 0f))
                val bitmap = safeImage.bitmapWithFilterApplied
                updateState { copy(heat = heat, updatedPhoto = bitmap, lastUpdatedPhotoWithBackground = bitmap) }
            }
        }
    }

    fun initImage(current: Context) {
        val gpuImage = GPUImage(current)
        gpuImage.setImage(uiState.value.initialPhoto)
        val bitmap = gpuImage.bitmapWithFilterApplied
        updateState { copy(updatedPhoto = bitmap, lastUpdatedPhotoWithBackground = bitmap, gpuImage = gpuImage) }
    }

    fun onRemoveBackground(remove: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            if (remove) {
                uiState.value.updatedPhoto?.let { safePhoto ->
                    updateState { copy(loading = true) }
                    val lastPhoto = safePhoto.copy(safePhoto.config, safePhoto.isMutable)
                    BackgroundRemover.bitmapForProcessing(
                        safePhoto,
                        false,
                        object : OnBackgroundChangeListener {
                            override fun onSuccess(bitmap: Bitmap) {
                                val gpuImage = uiState.value.gpuImage
                                gpuImage?.setImage(bitmap)
                                updateState {
                                    copy(
                                        updatedPhoto = gpuImage?.bitmapWithFilterApplied,
                                        lastUpdatedPhotoWithBackground = lastPhoto,
                                        gpuImage = gpuImage,
                                        loading = false
                                    )
                                }
                            }

                            override fun onFailed(exception: Exception) {
                                updateState { copy(loading = false) }
                                //exception
                            }
                        }
                    )
                }
            } else {
                val gpuImage = uiState.value.gpuImage
                gpuImage?.setImage(uiState.value.lastUpdatedPhotoWithBackground)
                updateState {
                    copy(
                        updatedPhoto = lastUpdatedPhotoWithBackground,
                        lastUpdatedPhotoWithBackground = lastUpdatedPhotoWithBackground,
                        gpuImage = gpuImage
                    )
                }
            }
        }
    }
}

data class EditPhotoViewState(
    val loading: Boolean = false,
    val lastCapturedPhotoPath: String? = null,
    val gpuImage: GPUImage? = null,
    val updatedPhoto: Bitmap? = null,
    val lastUpdatedPhotoWithBackground: Bitmap? = null,
    val initialPhoto: Bitmap? = null,
    val sharpness: Float = 1f,
    val brightness: Float = 0f,
    val heat: Float = 5000f
) : IViewState

sealed interface EditPhotoViewEvent : IViewEvents {
    data object PhotoReadCompleted : EditPhotoViewEvent
}