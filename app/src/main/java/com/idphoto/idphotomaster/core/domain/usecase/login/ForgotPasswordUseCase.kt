package com.idphoto.idphotomaster.core.domain.usecase.login

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import getExceptionOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(email: String): Flow<Unit> {
        return flow {
            val result = userRepository.forgotPassword(email)
            (result.getOrNull() ?: throw result.getExceptionOrDefault()).also {
                emit(it)
            }
        }
    }
}