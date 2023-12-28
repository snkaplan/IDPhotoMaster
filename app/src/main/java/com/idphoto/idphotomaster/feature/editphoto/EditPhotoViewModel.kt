package com.idphoto.idphotomaster.feature.editphoto

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
                            updateState {
                                copy(
                                    lastCapturedPhotoPath = photoArgs.photoPath,
                                    lastCapturedPhoto = result.data
                                )
                            }
                        }
                    }
                }.launchIn(this)
        }
    }
}

data class EditPhotoViewState(
    val loading: Boolean = false,
    val lastCapturedPhotoPath: String? = null,
    val lastCapturedPhoto: Bitmap? = null
) : IViewState

sealed class EditPhotoViewEvent : IViewEvents {}