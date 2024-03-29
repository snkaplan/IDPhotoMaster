package com.idphoto.idphotomaster.core.domain.usecase.home

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.idphoto.idphotomaster.core.domain.exceptions.GeneralException
import dagger.hilt.android.qualifiers.ApplicationContext
import getExceptionOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.OutputStream
import javax.inject.Inject

class SavePhotoToGalleryUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(capturePhotoBitmap: Bitmap): Flow<Uri> {
        return flow {
            val resolver: ContentResolver = context.applicationContext.contentResolver

            val imageCollection: Uri = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )

                else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val nowTimestamp: Long = System.currentTimeMillis()

            val imageContentValues: ContentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "IDPhoto-${System.currentTimeMillis()}" + ".png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.DATE_TAKEN, nowTimestamp)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DCIM + "/IDPhotoMaster"
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    put(MediaStore.Images.Media.DATE_TAKEN, nowTimestamp)
                    put(MediaStore.Images.Media.DATE_ADDED, nowTimestamp)
                    put(MediaStore.Images.Media.DATE_MODIFIED, nowTimestamp)
                    put(MediaStore.Images.Media.AUTHOR, "IDSheepShock")
                    put(MediaStore.Images.Media.DESCRIPTION, "IDSheepShock-Photo")
                }
            }

            val imageMediaStoreUri: Uri? = resolver.insert(imageCollection, imageContentValues)
            val result: Result<Uri> = imageMediaStoreUri?.let { uri ->
                kotlin.runCatching {
                    resolver.openOutputStream(uri).use { outputStream: OutputStream? ->
                        checkNotNull(outputStream) { "Couldn't create file for gallery, MediaStore output stream is null" }
                        capturePhotoBitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            100,
                            outputStream
                        )
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        imageContentValues.clear()
                        imageContentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                        resolver.update(uri, imageContentValues, null, null)
                    }

                    Result.success(imageMediaStoreUri)
                }.getOrElse { exception: Throwable ->
                    exception.message?.let(::println)
                    resolver.delete(uri, null, null)
                    Result.failure(exception)
                }
            } ?: run {
                throw GeneralException()
            }
            (result.getOrNull() ?: throw result.getExceptionOrDefault()).also {
                emit(it)
            }
        }
    }
}