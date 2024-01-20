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
import com.idphoto.idphotomaster.core.common.extension.applyFilters
import com.idphoto.idphotomaster.core.data.util.ImageSegmentationHelper
import com.idphoto.idphotomaster.core.domain.usecase.home.ReadImageFromGalleryUseCase
import com.idphoto.idphotomaster.core.domain.usecase.home.SavePhotoToGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
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
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase,
) : BaseViewModel<EditPhotoViewState, EditPhotoViewEvent>() {

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
                            val converted: Bitmap = result.data.copy(Bitmap.Config.ARGB_8888, result.data.isMutable)
                            updateState {
                                copy(
                                    initialPhotoPath = photoPath,
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
                safeImage.setFilter(getFilters(brightness = brightness))
                updateState {
                    copy(
                        brightness = brightness,
                        updatedPhoto = safeImage.bitmapWithFilterApplied
                    )
                }
            }
        }
    }

    fun onSharpnessChanged(sharpness: Float) {
        uiState.value.gpuImage?.let { safeImage ->
            viewModelScope.launch(ioDispatcher) {
                safeImage.setFilter(getFilters(sharpness = sharpness))
                updateState {
                    copy(
                        sharpness = sharpness,
                        updatedPhoto = safeImage.bitmapWithFilterApplied
                    )
                }
            }
        }
    }

    fun onHeatChanged(heat: Float) {
        uiState.value.gpuImage?.let { safeImage ->
            viewModelScope.launch(ioDispatcher) {
                safeImage.setFilter(getFilters(heat = heat))
                updateState {
                    copy(
                        heat = heat,
                        updatedPhoto = safeImage.bitmapWithFilterApplied
                    )
                }
            }
        }
    }

    fun initImage(context: Context) {
        viewModelScope.launch(ioDispatcher) {
            val gpuImage = GPUImage(context)
            gpuImage.setImage(uiState.value.initialPhoto)
            gpuImage.applyFilters(getFilters())
            val bitmap = gpuImage.bitmapWithFilterApplied
            updateState { copy(updatedPhoto = bitmap, gpuImage = gpuImage) }
        }
    }

    private fun getFilters(
        heat: Float = uiState.value.heat,
        sharpness: Float = uiState.value.sharpness,
        brightness: Float = uiState.value.brightness
    ): GPUImageFilterGroup {
        val filters = listOf(
            GPUImageWhiteBalanceFilter(heat, 0f),
            GPUImageSharpenFilter(sharpness),
            GPUImageBrightnessFilter(brightness)
        )
        return GPUImageFilterGroup(filters)
    }

    fun onRemoveBackground(remove: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            if (remove) {
                uiState.value.updatedPhoto?.let { safePhoto ->
                    updateState { copy(loading = true) }
                    val output = ImageSegmentationHelper.getResult(safePhoto)
                    val gpuImage = uiState.value.gpuImage
                    gpuImage?.setImage(output)
                    updateState {
                        copy(
                            updatedPhoto = gpuImage?.bitmapWithFilterApplied,
                            gpuImage = gpuImage,
                            loading = false
                        )
                    }
                }
            } else {
                fireEvent(EditPhotoViewEvent.ResetImage)
            }
        }
    }

    fun storePhotoInGallery() {
        viewModelScope.launch(ioDispatcher) {
            uiState.value.updatedPhoto?.let {
                savePhotoToGalleryUseCase(it).asResource()
                    .onEach { result ->
                        when (result) {
                            Resource.Loading -> {
                                updateState { copy(loading = true) }
                            }

                            is Resource.Error -> {
                                updateState { copy(loading = false) }
                            }

                            is Resource.Success -> {
                                updateState {
                                    copy(loading = false)
                                }
                            }
                        }
                    }.launchIn(this)
            }
        }
    }
}

data class EditPhotoViewState(
    val loading: Boolean = false,
    val initialPhotoPath: String? = null,
    val gpuImage: GPUImage? = null,
    val updatedPhoto: Bitmap? = null,
    val initialPhoto: Bitmap? = null,
    val sharpness: Float = 1f,
    val brightness: Float = 0f,
    val heat: Float = 5000f
) : IViewState

sealed interface EditPhotoViewEvent : IViewEvents {
    data object PhotoReadCompleted : EditPhotoViewEvent
    data object ResetImage : EditPhotoViewEvent
}