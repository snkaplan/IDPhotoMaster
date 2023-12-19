package com.idphoto.idphotomaster.core.domain.login

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor() {
    operator fun invoke(username: String, password: String): Flow<LoginResult> {
        return flow {
        }
    }
}