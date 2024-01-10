package com.idphoto.idphotomaster.core.domain.usecase.home

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class SaveImageToTempFile @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(capturePhotoBitmap: Bitmap): Flow<Uri> {
        return flow<Uri> {
            val outputDir = context.cacheDir // context being the Activity pointer
            val outputFile = File.createTempFile("tempPhoto", ".png", outputDir)
            val result: Result<Uri> = try {
                FileOutputStream(outputFile).use { out ->
                    capturePhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // bmp is your Bitmap instance
                }
                Result.success(outputFile.toUri())
            } catch (e: IOException) {
                e.printStackTrace()
                outputFile.deleteOnExit()
                Result.failure(e)
            }
            (result.getOrNull() ?: throw Exception("Couldn't create file for gallery")).also {
                emit(it)
                outputFile.deleteOnExit()
            }
        }.flowOn(Dispatchers.IO)
    }
}