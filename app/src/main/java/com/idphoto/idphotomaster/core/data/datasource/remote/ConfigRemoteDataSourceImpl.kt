package com.idphoto.idphotomaster.core.data.datasource.remote

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.idphoto.idphotomaster.core.common.await
import com.idphoto.idphotomaster.core.data.util.NetworkMonitor
import javax.inject.Inject

class ConfigRemoteDataSourceImpl @Inject constructor(
    private val config: FirebaseRemoteConfig,
    private val networkMonitor: NetworkMonitor
) :
    ConfigRemoteDataSource {
    override suspend fun getConfig(): Result<FirebaseRemoteConfig> {
        return runCatching {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 60
            }
            config.setConfigSettingsAsync(configSettings)
            config.fetchAndActivate().await(networkMonitor)
            config
        }
    }
}