package com.idphoto.idphotomaster.core.systemdesign.utils

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun takePicture(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onImageCaptured: (ImageProxy) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val imageCapture = ImageCapture.Builder().setResolutionSelector(
        ResolutionSelector.Builder().setAspectRatioStrategy(
            AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
        ).build()
    ).build()
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture)
            imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    onImageCaptured(image)
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    onError(exception)
                }
            })
        } catch (e: Exception) {
            Log.e("takePicture", "Binding failed", e)
        }
    }, ContextCompat.getMainExecutor(context))
}