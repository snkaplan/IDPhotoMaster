package com.idphoto.idphotomaster.core.domain.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.idphoto.idphotomaster.core.Session
import com.idphoto.idphotomaster.core.common.Constants
import com.idphoto.idphotomaster.core.common.suspendSafeLet
import com.idphoto.idphotomaster.core.domain.usecase.basket.CompletePurchaseUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SuccessPurchaseWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val completePurchaseUseCase: CompletePurchaseUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val userId: String? = inputData.getString(Constants.USER_ID)
            suspendSafeLet(userId, Session.uploadPhoto) { uid, photo ->
                val bitmap = photo.copy(photo.config, photo.isMutable)
                completePurchaseUseCase.invoke(uid, bitmap)
                Session.uploadPhoto = null
            }
        } catch (e: Exception) {
            Log.e("TAG", "doWork:", e)
        }
        return Result.success()
    }
}