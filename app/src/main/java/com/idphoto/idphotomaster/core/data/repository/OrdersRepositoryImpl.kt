package com.idphoto.idphotomaster.core.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.idphoto.idphotomaster.core.data.datasource.remote.OrdersRemoteDataSource
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(private val ordersRemoteDataSource: OrdersRemoteDataSource) :
    OrdersRepository {
    override suspend fun getUserPurchases(uid: String): Result<List<DocumentSnapshot>> {
        return ordersRemoteDataSource.getUserPurchases(uid)
    }
}