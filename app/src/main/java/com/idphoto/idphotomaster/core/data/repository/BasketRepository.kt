package com.idphoto.idphotomaster.core.data.repository

import android.net.Uri
import com.idphoto.idphotomaster.core.domain.model.Purchase

interface BasketRepository {
    suspend fun purchase(uid: String, purchase: Purchase): Result<Unit>
    suspend fun uploadPhoto(fileName: String, image: ByteArray): Result<Uri>
}