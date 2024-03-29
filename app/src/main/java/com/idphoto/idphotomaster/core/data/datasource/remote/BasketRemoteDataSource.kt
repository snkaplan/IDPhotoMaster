package com.idphoto.idphotomaster.core.data.datasource.remote

import android.net.Uri
import com.google.firebase.firestore.DocumentReference

interface BasketRemoteDataSource {
    suspend fun purchase(
        uid: String, purchase: MutableMap<String, Any?>, documentReference: DocumentReference
    ): Result<Unit>

    suspend fun uploadPhoto(uid: String, fileName: String, image: ByteArray): Result<Uri>
    suspend fun deletePurchase(userId: String, fileName: String, docId: String): Result<Unit>
}