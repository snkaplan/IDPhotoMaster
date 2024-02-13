package com.idphoto.idphotomaster.core.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.idphoto.idphotomaster.core.data.datasource.remote.ConfigRemoteDataSource
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(private val configRemoteDataSource: ConfigRemoteDataSource) :
    ConfigRepository {
    private lateinit var remoteConfig: FirebaseRemoteConfig
    override suspend fun getConfig(): Result<FirebaseRemoteConfig> {
        return if (::remoteConfig.isInitialized) {
            Result.success(remoteConfig)
        } else configRemoteDataSource.getConfig()
    }
}