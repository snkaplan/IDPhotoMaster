package com.idphoto.idphotomaster.core.data.repository

import com.idphoto.idphotomaster.core.data.datasource.remote.BasketRemoteDataSource
import javax.inject.Inject

class BasketRepositoryImpl @Inject constructor(private val basketRemoteDataSource: BasketRemoteDataSource) :
    BasketRepository {
    override suspend fun purchase(): Result<Unit> {
        return basketRemoteDataSource.purchase()
    }

    override suspend fun uploadPhoto(): Result<Unit> {
        return basketRemoteDataSource.uploadPhoto()
    }
}