package com.idphoto.idphotomaster.core.domain.usecase.profile

import com.idphoto.idphotomaster.core.data.repository.UserRepository
import getExceptionOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<Unit> {
        return flow {
            val result = userRepository.deleteUser()
            (result.getOrNull() ?: throw result.getExceptionOrDefault()).also {
                emit(it)
            }
        }
    }
}