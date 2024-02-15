package com.idphoto.idphotomaster.core.domain.usecase.profile

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<Unit> {
        return flow {
            val result = userRepository.signOut()
            (result.getOrNull() ?: throw IllegalArgumentException("error message")).also {
                emit(it)
            }
        }
    }
}