package com.idphoto.idphotomaster.core.data.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import com.idphoto.idphotomaster.core.data.datasource.remote.BasketRemoteDataSource
import com.idphoto.idphotomaster.core.domain.model.Purchase
import javax.inject.Inject

class BasketRepositoryImpl @Inject constructor(private val basketRemoteDataSource: BasketRemoteDataSource) :
    BasketRepository {
    override suspend fun purchase(
        uid: String,
        purchase: Purchase,
        documentReference: DocumentReference
    ): Result<Unit> {
        return basketRemoteDataSource.purchase(uid, purchase.toFirebaseMap(), documentReference)
    }

    override suspend fun uploadPhoto(fileName: String, image: ByteArray): Result<Uri> {
        return basketRemoteDataSource.uploadPhoto(fileName, image)
    }

    override suspend fun deletePurchase(userId: String, id: String): Result<Unit> {
        return basketRemoteDataSource.deletePurchase(userId, id)
    }
}