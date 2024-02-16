package com.idphoto.idphotomaster.core.domain.usecase.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.idphoto.idphotomaster.core.data.repository.ConfigRepository
import com.idphoto.idphotomaster.core.domain.exceptions.GeneralException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetConfigUseCase @Inject constructor(private val configRepository: ConfigRepository) {
    operator fun invoke(): Flow<FirebaseRemoteConfig> {
        return flow {
            val result = configRepository.getConfig()
            (result.getOrNull() ?: throw GeneralException()).also {
                emit(it)
            }
        }
    }
}