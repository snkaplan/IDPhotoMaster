package com.idphoto.idphotomaster.core.domain.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.idphoto.idphotomaster.R
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

    private val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = System.currentTimeMillis().toInt()

    override suspend fun doWork(): Result {
        try {
            setForegroundAsync(createForegroundInfo())
            val userId: String? = inputData.getString(Constants.USER_ID)
            suspendSafeLet(userId, Session.uploadPhoto) { uid, photo ->
                val bitmap = photo.copy(photo.config, photo.isMutable)
                completePurchaseUseCase.invoke(uid, bitmap)
                Session.uploadPhoto = null
            }

        } catch (e: Exception) {
            Log.e("TAG", "doWork:", e)
        }
        notificationManager.cancel(notificationId)
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val contentText = applicationContext.getString(R.string.notification_content_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_upload)
            .setSilent(true)
            .setOngoing(true)
            .build()
        notificationManager.notify(notificationId, notification)
        return ForegroundInfo(System.currentTimeMillis().toInt(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = applicationContext.getString(R.string.notification_channel_id)
        val descriptionText = applicationContext.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel =
            NotificationChannel(applicationContext.getString(R.string.notification_channel_id), name, importance)
        mChannel.description = descriptionText
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}