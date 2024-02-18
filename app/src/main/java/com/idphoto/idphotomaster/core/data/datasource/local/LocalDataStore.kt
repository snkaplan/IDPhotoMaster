package com.idphoto.idphotomaster.core.data.datasource.local

import kotlinx.coroutines.flow.Flow

interface LocalDataStore {
    fun isUserSawTutorial(): Flow<Boolean>
    suspend fun setUserSawTutorial(seen: Boolean)
    fun isUserSawCameraTutorial(): Flow<Boolean>
    suspend fun setUserSawCameraTutorial(seen: Boolean)
}