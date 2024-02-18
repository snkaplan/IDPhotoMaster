package com.idphoto.idphotomaster.feature.home.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.common.dispatchers.AppDispatchers
import com.idphoto.idphotomaster.core.common.dispatchers.Dispatcher
import com.idphoto.idphotomaster.core.data.datasource.local.LocalDataStore
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.usecase.home.SaveImageToTempFile
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import getExceptionModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val saveImageToTempFile: SaveImageToTempFile,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val localDataStore: LocalDataStore
) : BaseViewModel<CameraViewState>() {
    override fun createInitialState(): CameraViewState = CameraViewState()

    init {
        viewModelScope.launch {
            localDataStore.isUserSawTutorial().collectLatest {
                if (!it) {
                    updateState { copy(showTutorialDialog = true) }
                }
                return@collectLatest
            }
        }
    }

    fun saveTempImage(bitmap: Bitmap) {
        viewModelScope.launch(ioDispatcher) {
            saveImageToTempFile(photoBitmap = bitmap).asResource()
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
                                    capturedImageUri = result.data,
                                    capturedImage = bitmap,
                                    navigateToEditPhoto = triggered(result.data.toString())
                                )
                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    override fun onCleared() {
        uiState.value.capturedImage?.recycle()
        super.onCleared()
    }

    fun onTriggerViewEvent(viewEvent: CameraViewEvent) {
        when (viewEvent) {
            CameraViewEvent.OnClickTutorial -> updateState { copy(showTutorialDialog = true) }
            CameraViewEvent.OnTutorialClosed -> {
                viewModelScope.launch {
                    localDataStore.setUserSawCameraTutorial(true)
                    updateState { copy(showTutorialDialog = false) }
                }
            }
        }
    }

    fun onNavigateToEditPhotoConsumed() {
        updateState { copy(navigateToEditPhoto = consumed()) }
    }

    fun onErrorDialogDismiss() {
        updateState { copy(exception = null) }
    }
}

data class CameraViewState(
    val loading: Boolean = false,
    val capturedImageUri: Uri? = null,
    val capturedImage: Bitmap? = null,
    val navigateToEditPhoto: StateEventWithContent<String> = consumed(),
    val showTutorialDialog: Boolean = false,
    val exception: ExceptionModel? = null
) : IViewState

sealed interface CameraViewEvent {
    data object OnClickTutorial : CameraViewEvent
    data object OnTutorialClosed : CameraViewEvent
}