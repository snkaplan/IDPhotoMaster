package com.idphoto.idphotomaster.feature.savedphotos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.common.dispatchers.AppDispatchers
import com.idphoto.idphotomaster.core.common.dispatchers.Dispatcher
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.UserSavedPhoto
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.usecase.home.SavePhotoToGalleryUseCase
import com.idphoto.idphotomaster.core.domain.usecase.profile.GetUserPurchases
import dagger.hilt.android.lifecycle.HiltViewModel
import getExceptionModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SavedPhotosViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserPurchases: GetUserPurchases,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase
) : BaseViewModel<SavedPhotosViewState>() {
    override fun createInitialState(): SavedPhotosViewState = SavedPhotosViewState()

    fun init() {
        userRepository.currentUser?.let {
            getUserPurchases(it.uid)
        }
    }

    fun onTriggerViewEvent(event: SavedPhotosViewEvent) {
        viewModelScope.launch {
            when (event) {
                is SavedPhotosViewEvent.OnBoughtPhotoClicked -> onBoughtPhotoClicked(event.context, event.url)
                SavedPhotosViewEvent.PhotoSavedDialogDismissed -> updateState { copy(showPhotoSavedDialog = false) }
            }
        }
    }

    private fun getUserPurchases(uid: String) {
        viewModelScope.launch {
            getUserPurchases.invoke(uid).asResource().onEach { result ->
                when (result) {
                    Resource.Loading -> {
                        updateState { copy(loading = true) }
                    }

                    is Resource.Error -> {
                        updateState {
                            copy(
                                loading = false, exception = result.exception?.getExceptionModel(
                                    descriptionResId = R.string.exception_fetch_user_purchases
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        updateState { copy(loading = false, savedPhotos = result.data) }
                    }
                }
            }.launchIn(this)
        }
    }

    private suspend fun onBoughtPhotoClicked(context: Context, url: String) {
        withContext(ioDispatcher) {
            if (currentState.loading) {
                return@withContext
            }
            updateState { copy(loading = true) }
            val bitmap: Bitmap
            val imageLoader = ImageLoader(context)
            val request = ImageRequest
                .Builder(context)
                .data(url)
                .build()
            imageLoader.execute(request).drawable?.let { drawable ->
                bitmap = (drawable as BitmapDrawable).bitmap
                saveBoughtImageToGallery(bitmap, this)
            } ?: run {
                updateState { copy(loading = false) }
            }
        }
    }

    private fun saveBoughtImageToGallery(bitmap: Bitmap, launchScope: CoroutineScope) {
        savePhotoToGalleryUseCase(capturePhotoBitmap = bitmap).asResource()
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
                        updateState { copy(loading = false, showPhotoSavedDialog = true) }
                    }
                }
            }.launchIn(launchScope)
    }

    fun onErrorDialogDismiss() {
        updateState { copy(exception = null) }
    }
}

data class SavedPhotosViewState(
    val loading: Boolean = false,
    val savedPhotos: List<UserSavedPhoto>? = null,
    val showPhotoSavedDialog: Boolean = false,
    val exception: ExceptionModel? = null
) : IViewState

sealed interface SavedPhotosViewEvent {
    data class OnBoughtPhotoClicked(val url: String, val context: Context) : SavedPhotosViewEvent
    data object PhotoSavedDialogDismissed : SavedPhotosViewEvent
}