package com.idphoto.idphotomaster.core.data.datasource.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

interface ConfigRemoteDataSource {
    suspend fun getConfig(): Result<FirebaseRemoteConfig>
}