package com.idphoto.idphotomaster.feature.savedphotos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
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
import com.idphoto.idphotomaster.core.domain.usecase.home.SaveImageToTempFile
import com.idphoto.idphotomaster.core.domain.usecase.profile.GetUserPurchases
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import getExceptionModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SavedPhotosViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserPurchases: GetUserPurchases,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val saveImageToTempFile: SaveImageToTempFile
) : BaseViewModel<SavedPhotosViewState>() {
    override fun createInitialState(): SavedPhotosViewState = SavedPhotosViewState()

    fun init() {
        userRepository.currentUser?.let {
            getUserPurchases(it.uid)
        }
    }

    fun onTriggerViewEvent(event: SavedPhotosViewEvent) {
        when (event) {
            is SavedPhotosViewEvent.OnSavedPhotoClicked -> onSavedPhotoClicked(event.path)
            is SavedPhotosViewEvent.OnBoughtPhotoClicked -> onBoughtPhotoClicked(event.context, event.url)
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

    private fun onSavedPhotoClicked(photoPath: String) {
        val uri = Uri.fromFile(File(photoPath))
        updateState { copy(navigateToEditPhoto = triggered(uri.toString())) }
    }

    private fun onBoughtPhotoClicked(context: Context, url: String) {
        viewModelScope.launch(ioDispatcher) {
            updateState { copy(loading = true) }
            val bitmap: Bitmap
            val imageLoader = ImageLoader(context)
            val request = ImageRequest
                .Builder(context)
                .data(url)
                .build()
            imageLoader.execute(request).drawable?.let { drawable ->
                bitmap = (drawable as BitmapDrawable).bitmap
                saveBoughtImageToTempFile(bitmap, this)
            }
        }
    }

    private fun saveBoughtImageToTempFile(bitmap: Bitmap, launchScope: CoroutineScope) {
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
                        updateState { copy(loading = false, navigateToEditPhoto = triggered(result.data.toString())) }
                    }
                }
            }.launchIn(launchScope)
    }

    fun onNavigateEditPhotoConsumed() {
        updateState { copy(navigateToEditPhoto = consumed()) }
    }

    fun onErrorDialogDismiss() {
        updateState { copy(exception = null) }
    }
}

data class SavedPhotosViewState(
    val loading: Boolean = false,
    val savedPhotos: List<UserSavedPhoto>? = null,
    val navigateToEditPhoto: StateEventWithContent<String> = consumed(),
    val exception: ExceptionModel? = null
) : IViewState

sealed interface SavedPhotosViewEvent {
    data class OnBoughtPhotoClicked(val url: String, val context: Context) : SavedPhotosViewEvent
    data class OnSavedPhotoClicked(val path: String) : SavedPhotosViewEvent
}
