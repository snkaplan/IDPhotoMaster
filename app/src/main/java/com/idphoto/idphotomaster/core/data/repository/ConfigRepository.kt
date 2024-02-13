package com.idphoto.idphotomaster.core.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

interface ConfigRepository {
    suspend fun getConfig(): Result<FirebaseRemoteConfig>
}