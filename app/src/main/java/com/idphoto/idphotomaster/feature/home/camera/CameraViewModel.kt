package com.idphoto.idphotomaster.feature.home.camera

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.domain.usecase.home.SavePhotoToGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase
) : BaseViewModel<CameraViewState, CameraViewEvents>() {
    override fun createInitialState(): CameraViewState = CameraViewState()

    fun storePhotoInGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            savePhotoToGalleryUseCase(bitmap)
            updateCapturedPhotoState(bitmap)
        }
    }

    private fun updateCapturedPhotoState(updatedPhoto: Bitmap?) {
        uiState.value.capturedImage?.recycle()
        updateState { copy(capturedImage = updatedPhoto) }
    }

    override fun onCleared() {
        uiState.value.capturedImage?.recycle()
        super.onCleared()
    }
}

data class CameraViewState(
    val loading: Boolean = false,
    val capturedImage: Bitmap? = null
) : IViewState

sealed class CameraViewEvents : IViewEvents {}