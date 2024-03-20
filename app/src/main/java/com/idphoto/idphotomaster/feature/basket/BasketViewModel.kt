package com.idphoto.idphotomaster.feature.basket

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.Session
import com.idphoto.idphotomaster.core.common.BaseViewModel
import com.idphoto.idphotomaster.core.common.Constants
import com.idphoto.idphotomaster.core.common.IViewState
import com.idphoto.idphotomaster.core.common.Resource
import com.idphoto.idphotomaster.core.common.asResource
import com.idphoto.idphotomaster.core.common.dispatchers.AppDispatchers
import com.idphoto.idphotomaster.core.common.dispatchers.Dispatcher
import com.idphoto.idphotomaster.core.common.suspendSafeLet
import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.base.ExceptionModel
import com.idphoto.idphotomaster.core.domain.usecase.home.ReadImageFromDevice
import com.idphoto.idphotomaster.core.domain.usecase.home.SavePhotoToGalleryUseCase
import com.idphoto.idphotomaster.core.domain.worker.SuccessPurchaseWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import getExceptionModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BasketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val readImageFromDevice: ReadImageFromDevice,
    private val userRepository: UserRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase,
) : BaseViewModel<BasketViewState>() {
    private val photoArgs: BasketArgs = BasketArgs(savedStateHandle)
    override fun createInitialState(): BasketViewState = BasketViewState()

    init {
        readImageAndUpdateState(photoArgs.selectedPhotoPath)
    }

    fun onTriggerViewEvent(event: BasketViewEvent) {
        viewModelScope.launch {
            when (event) {
                is BasketViewEvent.OnPurchaseSuccess -> {
                    suspendSafeLet(userRepository.currentUser?.uid, uiState.value.photo) { uid, photo ->
                        uploadPhoto(applicationContext = event.context, userId = uid, photo = photo)
                    }
                    purchaseSuccess()
                }

                BasketViewEvent.OnErrorDialogDismiss -> updateState { copy(exception = null) }
                BasketViewEvent.OnPurchaseCompletedConsumed -> updateState { copy(purchaseCompleted = consumed) }
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
                                        descriptionResId = R.string.exception_read_image
                                    )
                                )
                            }
                        }

                        is Resource.Success -> {
                            val converted: Bitmap = result.data.copy(Bitmap.Config.ARGB_8888, false)
                            updateState {
                                copy(
                                    photo = converted,
                                    loading = false
                                )
                            }
                        }
                    }
                }.launchIn(this)
        }
    }


    private suspend fun purchaseSuccess() {
        withContext(currentCoroutineContext()) {
            uiState.value.photo?.let {
                savePhotoToGalleryUseCase(capturePhotoBitmap = it).asResource().onEach { result ->
                    when (result) {
                        is Resource.Error -> {
                            updateState {
                                copy(
                                    loading = false, exception = result.exception?.getExceptionModel(
                                        descriptionResId = R.string.exception_save_image_to_gallery
                                    )
                                )
                            }
                        }

                        Resource.Loading -> {
                            updateState { copy(loading = true) }
                        }

                        is Resource.Success -> {
                            updateState { copy(loading = false, purchaseCompleted = triggered) }
                        }
                    }
                }.launchIn(this)
            }
        }
    }

    private fun uploadPhoto(applicationContext: Context, userId: String, photo: Bitmap) {
        Session.uploadPhoto = photo
        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        val data: Data = Data.Builder()
            .putString(Constants.USER_ID, userId)
            .build()
        val uploadRequest =
            OneTimeWorkRequest.Builder(SuccessPurchaseWorker::class.java)
                .setConstraints(constraints)
                .setInputData(data)
                .build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(uploadRequest)
    }
}

data class BasketViewState(
    val loading: Boolean = false,
    val photo: Bitmap? = null,
    val exception: ExceptionModel? = null,
    val purchaseCompleted: StateEvent = consumed
) : IViewState

sealed interface BasketViewEvent {
    data class OnPurchaseSuccess(val context: Context) : BasketViewEvent
    data object OnPurchaseCompletedConsumed : BasketViewEvent
    data object OnErrorDialogDismiss : BasketViewEvent
}