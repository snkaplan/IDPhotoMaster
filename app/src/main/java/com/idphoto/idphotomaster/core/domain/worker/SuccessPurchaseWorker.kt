package com.idphoto.idphotomaster.core.domain.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.idphoto.idphotomaster.core.common.Constants
import com.idphoto.idphotomaster.core.domain.usecase.basket.StartPurchaseUseCase
import javax.inject.Inject

class SuccessPurchaseWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {

    @Inject
    lateinit var startPurchaseUseCase: StartPurchaseUseCase
    override fun doWork(): Result {
        try {
            val userId: String? = inputData.getString(Constants.USER_ID)
            val photoPath: String? = inputData.getString(Constants.PHOTO_PATH)
        } catch (e: Exception) {
            Log.e("TAG", "doWork:", e)
        }
        return Result.success()
    }
}