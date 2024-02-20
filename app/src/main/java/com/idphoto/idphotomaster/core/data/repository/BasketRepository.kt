package com.idphoto.idphotomaster.core.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.idphoto.idphotomaster.core.domain.model.Purchase

interface BasketRepository {
    suspend fun purchase(
        uid: String,
        purchase: Purchase,
        documentReference: DocumentReference
    ): Result<Unit>

    suspend fun uploadPhoto(uid: String, fileName: String, image: ByteArray): Result<Uri>
    suspend fun deletePurchase(userId: String, fileName: String, docId: String): Result<Unit>
}