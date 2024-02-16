package com.idphoto.idphotomaster.core.data.datasource.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.idphoto.idphotomaster.core.common.Constants
import com.idphoto.idphotomaster.core.common.await
import javax.inject.Inject

class OrdersRemoteDataSourceImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : OrdersRemoteDataSource {
    override suspend fun getUserPurchases(uid: String): Result<List<DocumentSnapshot>> {
        return runCatching {
            val result = firebaseFirestore.collection(Constants.USERS_TABLE_NAME).document(uid)
                .collection(Constants.PURCHASE_TABLE_NAME).get().await()
            result.documents
        }
    }
}