package com.idphoto.idphotomaster.core.data.datasource.remote

interface BasketRemoteDataSource {
    suspend fun purchase(): Result<Unit>
    suspend fun uploadPhoto(): Result<Unit>
}