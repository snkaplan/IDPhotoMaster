package com.idphoto.idphotomaster.feature.editphoto

import android.content.Context
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
import com.idphoto.idphotomaster.core.common.extension.applyFilters
import com.idphoto.idphotomaster.core.data.util.ImageSegmentationHelper
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.usecase.home.ReadImageFromDevice
import com.idphoto.idphotomaster.core.domain.usecase.home.SaveImageToTempFile
import com.idphoto.idphotomaster.core.domain.usecase.home.SavePhotoToCache
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import getExceptionModel
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditPhotoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val readImageFromDevice: ReadImageFromDevice,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val savePhotoToCache: SavePhotoToCache,
    private val saveImageToTempFile: SaveImageToTempFile
) : BaseViewModel<EditPhotoViewState>() {

    private val photoArgs: EditPhotoArgs = EditPhotoArgs(savedStateHandle)
    override fun createInitialState(): EditPhotoViewState = EditPhotoViewState()

    init {
        readImageAndUpdateState(photoArgs.photoPath)
    }

    fun onTriggerViewEvent(event: EditPhotoViewEvent) {
        viewModelScope.launch {
            when (event) {
                is EditPhotoViewEvent.OnBrightnessChange -> onBrightnessChanged(event.brightness)
                EditPhotoViewEvent.OnSavePhoto -> savePhoto()
                is EditPhotoViewEvent.OnHeatChange -> onHeatChanged(event.heat)
                is EditPhotoViewEvent.OnSharpnessChange -> onSharpnessChanged(event.sharpness)
                is EditPhotoViewEvent.InitImage -> initImage(event.context)
                is EditPhotoViewEvent.OnRemoveBackground -> onRemoveBackground(event.remove)
                EditPhotoViewEvent.OnNavigateToBasket -> navigateToBasket()
                EditPhotoViewEvent.OnErrorDialogDismissed -> updateState { copy(exception = null) }
                EditPhotoViewEvent.OnNavigateToBasketConsumed -> updateState { copy(navigateToBasket = consumed()) }
                EditPhotoViewEvent.OnPhotoReadConsumed -> updateState { copy(photoReadCompleted = consumed) }
                EditPhotoViewEvent.OnResetImageConsumed -> updateState { copy(resetImage = consumed) }
            }
        }
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
                                        descriptionResId = R.string.exception_read_image,
                                        primaryButtonTextResId = null
                                    )
                                )
                            }
                        }

                        is Resource.Success -> {
                            val converted: Bitmap = result.data.copy(Bitmap.Config.ARGB_8888, result.data.isMutable)
                            updateState {
                                copy(
                                    initialPhotoPath = photoPath,
                                    initialPhoto = converted,
                                    loading = false,
                                    photoReadCompleted = triggered
                                )
                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    private suspend fun onBrightnessChanged(brightness: Float) {
        uiState.value.gpuImage?.let { safeImage ->
            withContext(ioDispatcher) {
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

    private suspend fun onSharpnessChanged(sharpness: Float) {
        uiState.value.gpuImage?.let { safeImage ->
            withContext(ioDispatcher) {
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

    private suspend fun onHeatChanged(heat: Float) {
        uiState.value.gpuImage?.let { safeImage ->
            withContext(ioDispatcher) {
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

    private suspend fun initImage(context: Context) {
        withContext(ioDispatcher) {
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

    private suspend fun onRemoveBackground(remove: Boolean) {
        withContext(ioDispatcher) {
            if (remove) {
                uiState.value.updatedPhoto?.let { safePhoto ->
                    updateState { copy(loading = true) }
                    val output = try {
                        ImageSegmentationHelper.getResult(safePhoto)
                    } catch (e: Exception) {
                        updateState {
                            copy(loading = false, backgroundRemoved = false)
                        }
                        return@withContext
                    }
                    val gpuImage = uiState.value.gpuImage
                    gpuImage?.setImage(output)
                    updateState {
                        copy(
                            updatedPhoto = gpuImage?.bitmapWithFilterApplied,
                            gpuImage = gpuImage,
                            loading = false,
                            backgroundRemoved = true
                        )
                    }
                }
            } else {
                updateState { copy(resetImage = triggered, backgroundRemoved = false) }
            }
        }
    }

    private suspend fun savePhoto() {
        withContext(ioDispatcher) {
            uiState.value.updatedPhoto?.let {
                savePhotoToCache(photoBitmap = it, photoPath = uiState.value.savedPhotoPath).asResource()
                    .onEach { result ->
                        when (result) {
                            Resource.Loading -> {
                                updateState { copy(loading = true) }
                            }

                            is Resource.Error -> {
                                updateState {
                                    copy(
                                        loading = false, exception = result.exception?.getExceptionModel(
                                            descriptionResId = R.string.exception_save_image
                                        )
                                    )
                                }
                            }

                            is Resource.Success -> {
                                updateState {
                                    copy(loading = false, savedPhotoPath = result.data.toString())
                                }
                            }
                        }
                    }.launchIn(this)
            }
        }
    }

    private suspend fun navigateToBasket() {
        withContext(ioDispatcher) {
            uiState.value.updatedPhoto?.let {
                saveImageToTempFile(photoBitmap = it).asResource()
                    .onEach { result ->
                        when (result) {
                            Resource.Loading -> {
                                updateState { copy(loading = true) }
                            }

                            is Resource.Error -> {
                                updateState {
                                    copy(
                                        loading = false, exception = result.exception?.getExceptionModel(
                                            descriptionResId = R.string.exception_save_image
                                        )
                                    )
                                }
                            }

                            is Resource.Success -> {
                                updateState {
                                    copy(
                                        loading = false,
                                        navigateToBasket = triggered(result.data.toString())
                                    )
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
    val savedPhotoPath: String? = null,
    val gpuImage: GPUImage? = null,
    val updatedPhoto: Bitmap? = null,
    val initialPhoto: Bitmap? = null,
    val sharpness: Float = 0f,
    val brightness: Float = 0f,
    val heat: Float = 5000f,
    val photoReadCompleted: StateEvent = consumed,
    val resetImage: StateEvent = consumed,
    val navigateToBasket: StateEventWithContent<String> = consumed(),
    val backgroundRemoved: Boolean = false,
    val exception: ExceptionModel? = null
) : IViewState

sealed interface EditPhotoViewEvent {
    data class OnSharpnessChange(val sharpness: Float) : EditPhotoViewEvent
    data class OnHeatChange(val heat: Float) : EditPhotoViewEvent
    data class OnBrightnessChange(val brightness: Float) : EditPhotoViewEvent
    data class InitImage(val context: Context) : EditPhotoViewEvent
    data class OnRemoveBackground(val remove: Boolean) : EditPhotoViewEvent
    data object OnSavePhoto : EditPhotoViewEvent
    data object OnNavigateToBasket : EditPhotoViewEvent
    data object OnErrorDialogDismissed : EditPhotoViewEvent
    data object OnPhotoReadConsumed : EditPhotoViewEvent
    data object OnResetImageConsumed : EditPhotoViewEvent
    data object OnNavigateToBasketConsumed : EditPhotoViewEvent
}