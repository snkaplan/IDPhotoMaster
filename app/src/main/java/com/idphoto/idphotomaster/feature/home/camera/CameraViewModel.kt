package com.idphoto.idphotomaster.feature.home.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewEvents
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.common.dispatchers.AppDispatchers
import com.idphoto.idphotomaster.core.common.dispatchers.Dispatcher
import com.idphoto.idphotomaster.core.domain.usecase.home.SaveImageToTempFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val saveImageToTempFile: SaveImageToTempFile,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<CameraViewState, CameraViewEvents>() {
    override fun createInitialState(): CameraViewState = CameraViewState()

    fun storePhotoInGallery(bitmap: Bitmap) {
        viewModelScope.launch(ioDispatcher) {
            saveImageToTempFile(bitmap).asResource()
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
                                copy(
                                    loading = false,
                                    capturedImageUri = result.data,
                                    capturedImage = bitmap
                                )
                            }
                            fireEvent(CameraViewEvents.NavigateToEditPhoto(result.data))
                        }
                    }
                }.launchIn(this)
        }
    }

    override fun onCleared() {
        uiState.value.capturedImage?.recycle()
        super.onCleared()
    }
}

data class CameraViewState(
    val loading: Boolean = false,
    val capturedImageUri: Uri? = null,
    val capturedImage: Bitmap? = null
) : IViewState

sealed interface CameraViewEvents : IViewEvents {
    data class NavigateToEditPhoto(val capturedImageUri: Uri) : CameraViewEvents
}