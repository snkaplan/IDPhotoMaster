package com.idphoto.idphotomaster.core.data.datasource.remote

import android.net.Uri

interface BasketRemoteDataSource {
    suspend fun purchase(purchase: MutableMap<String, Any?>): Result<Unit>
    suspend fun uploadPhoto(fileName: String, image: ByteArray): Result<Uri>
}