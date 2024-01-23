package com.idphoto.idphotomaster.core.data.repository

import com.google.firebase.firestore.DocumentSnapshot

interface OrdersRepository {
    suspend fun getUserPurchases(uid: String): Result<List<DocumentSnapshot>>
}