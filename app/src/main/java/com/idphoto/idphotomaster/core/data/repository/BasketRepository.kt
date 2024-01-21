package com.idphoto.idphotomaster.core.data.repository

interface BasketRepository {
    suspend fun purchase(): Result<Unit>
    suspend fun uploadPhoto(): Result<Unit>
}