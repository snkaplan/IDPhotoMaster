package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import com.idphoto.idphotomaster.core.domain.model.LoginResult
import getExceptionOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(mail: String, password: String): Flow<LoginResult> {
        return flow {
            val result = userRepository.login(mail, password)
            (result.getOrNull() ?: throw result.getExceptionOrDefault()).also {
                emit(LoginResult(it.uid, it.uid))
            }
        }
    }
}