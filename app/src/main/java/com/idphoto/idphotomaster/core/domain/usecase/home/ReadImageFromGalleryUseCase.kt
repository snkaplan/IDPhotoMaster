package com.idphoto.idphotomaster.core.domain.usecase.home

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReadImageFromGalleryUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(imagePath: String): Flow<Bitmap> {
        return flow {
            val resolver: ContentResolver = context.applicationContext.contentResolver
            val result: Result<Bitmap> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Result.success(
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            resolver,
                            Uri.parse(imagePath)
                        )
                    )
                )
            } else {
                Result.success(
                    MediaStore.Images.Media.getBitmap(
                        resolver,
                        Uri.parse(imagePath)
                    )
                )
            }
            (result.getOrNull() ?: throw Exception("Couldn't create file for gallery")).also {
                emit(it)
            }
        }
    }
}