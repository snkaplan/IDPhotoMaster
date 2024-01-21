package com.idphoto.idphotomaster.core.data.datasource.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import javax.inject.Inject

class BasketRemoteDataSourceImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : BasketRemoteDataSource {
    override suspend fun purchase(): Result<Unit> {
        return runCatching {
            delay(6000)
        }
    }

    override suspend fun uploadPhoto(): Result<Unit> {
        return runCatching {
            delay(7000)
        }
    }
}