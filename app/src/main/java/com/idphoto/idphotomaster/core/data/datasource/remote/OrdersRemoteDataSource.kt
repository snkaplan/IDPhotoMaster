package com.idphoto.idphotomaster.core.data.datasource.remote

import com.google.firebase.firestore.DocumentSnapshot

interface OrdersRemoteDataSource {
    suspend fun getUserPurchases(uid: String): Result<List<DocumentSnapshot>>
}